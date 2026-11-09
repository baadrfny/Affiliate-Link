package com.affiliateLink.offer.service;

import com.affiliateLink.common.ConflictException;
import com.affiliateLink.common.ResourceNotFoundException;
import com.affiliateLink.offer.dto.CampaignResponse;
import com.affiliateLink.offer.dto.CreateCampaignRequest;
import com.affiliateLink.offer.entity.Campaign;
import com.affiliateLink.offer.entity.Offer;
import com.affiliateLink.offer.entity.enumeration.CampaignStatus;
import com.affiliateLink.offer.mapper.CampaignMapper;
import com.affiliateLink.offer.repository.CampaignRepository;
import com.affiliateLink.offer.repository.OfferRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class CampaignService {

    private static final Logger log = LoggerFactory.getLogger(CampaignService.class);

    private final CampaignRepository campaignRepository;
    private final OfferRepository offerRepository;
    private final CampaignMapper campaignMapper;
    private final AdvertiserChecker advertiserChecker;

    public CampaignService(CampaignRepository campaignRepository,
                           OfferRepository offerRepository,
                           CampaignMapper campaignMapper,
                           AdvertiserChecker advertiserChecker) {
        this.campaignRepository = campaignRepository;
        this.offerRepository = offerRepository;
        this.campaignMapper = campaignMapper;
        this.advertiserChecker = advertiserChecker;
    }

    @Transactional
    public CampaignResponse createCampaign(Long offerId, CreateCampaignRequest request) {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new ResourceNotFoundException("Offer not found with id: " + offerId));

        advertiserChecker.checkCanPublish(offer.getAdvertiserId());

        Campaign campaign = campaignMapper.toEntity(request);
        campaign.validateRules();          // 400 if dates / budget / payout are wrong
        offer.addCampaign(campaign);

        Campaign saved = campaignRepository.save(campaign);
        log.info("Campaign created: id={}, offerId={}, payoutModel={}",
                saved.getId(), offerId, saved.getPayoutModel());
        return campaignMapper.toResponse(saved);
    }

    public List<CampaignResponse> getCampaigns(CampaignStatus status) {
        return campaignMapper.toResponses(campaignRepository.findByStatus(status));
    }

    /**
     * Atomic: if activating fails at any step, the previous active campaign
     * stays active (the whole transaction is rolled back).
     */
    @Transactional
    public void changeStatus(Long campaignId, CampaignStatus target) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found with id: " + campaignId));

        LocalDate today = LocalDate.now();

        // 1. Check first, change nothing yet
        try {
            campaign.checkTransition(target, today);
        } catch (ConflictException e) {
            log.warn("Status change refused for campaign {}: {}", campaignId, e.getMessage());
            throw e;
        }

        // 2. Only one active campaign per offer: pause the old one first
        if (target == CampaignStatus.ACTIVE) {
            pauseOtherActiveCampaigns(campaign, today);
        }

        // 3. Now activate / pause / end this campaign
        campaign.changeStatus(target, today);
        log.info("Campaign {} is now {}", campaignId, target);
    }

    /**
     * Called when an advertiser is suspended.
     * Returns the titles of the offers whose campaigns were paused.
     */
    @Transactional
    public List<String> pauseActiveCampaignsOfAdvertiser(Long advertiserId) {
        List<Campaign> activeCampaigns =
                campaignRepository.findByAdvertiserIdAndStatus(advertiserId, CampaignStatus.ACTIVE);

        LocalDate today = LocalDate.now();
        Set<String> offerTitles = new LinkedHashSet<>();

        for (Campaign campaign : activeCampaigns) {
            campaign.changeStatus(CampaignStatus.PAUSED, today);
            offerTitles.add(campaign.getOffer().getTitle());
        }

        log.info("Paused {} active campaign(s) of suspended advertiser {}",
                activeCampaigns.size(), advertiserId);
        return new ArrayList<>(offerTitles);
    }

    private void pauseOtherActiveCampaigns(Campaign campaignToActivate, LocalDate today) {
        Long offerId = campaignToActivate.getOffer().getId();

        for (Campaign other : campaignRepository.findByOfferIdAndStatus(offerId, CampaignStatus.ACTIVE)) {
            if (!other.getId().equals(campaignToActivate.getId())) {
                other.changeStatus(CampaignStatus.PAUSED, today);
                // flush now: the database must see the old one PAUSED before the new one is ACTIVE
                campaignRepository.saveAndFlush(other);
                log.info("Campaign {} paused automatically (campaign {} activated on the same offer)",
                        other.getId(), campaignToActivate.getId());
            }
        }
    }
}
