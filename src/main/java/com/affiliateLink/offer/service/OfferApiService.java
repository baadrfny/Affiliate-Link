package com.affiliateLink.offer.service;

import com.affiliateLink.offer.CampaignInfo;
import com.affiliateLink.offer.OfferApi;
import com.affiliateLink.offer.mapper.CampaignMapper;
import com.affiliateLink.offer.repository.CampaignRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
class OfferApiService implements OfferApi {

    private static final Logger log = LoggerFactory.getLogger(OfferApiService.class);

    private final CampaignRepository campaignRepository;
    private final CampaignMapper campaignMapper;

    OfferApiService(CampaignRepository campaignRepository, CampaignMapper campaignMapper) {
        this.campaignRepository = campaignRepository;
        this.campaignMapper = campaignMapper;
    }

    @Override
    public Optional<CampaignInfo> getCampaign(Long campaignId) {
        log.debug("OfferApi: reading campaign {}", campaignId);
        return campaignRepository.findById(campaignId).map(campaignMapper::toInfo);
    }
}
