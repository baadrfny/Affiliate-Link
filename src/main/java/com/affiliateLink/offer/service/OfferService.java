package com.affiliateLink.offer.service;

import com.affiliateLink.common.BadRequestException;
import com.affiliateLink.common.PageResponse;
import com.affiliateLink.common.ResourceNotFoundException;
import com.affiliateLink.offer.dto.CreateOfferRequest;
import com.affiliateLink.offer.dto.OfferDetailResponse;
import com.affiliateLink.offer.dto.OfferResponse;
import com.affiliateLink.offer.dto.UpdateOfferRequest;
import com.affiliateLink.offer.entity.Offer;
import com.affiliateLink.offer.entity.Tag;
import com.affiliateLink.offer.entity.enumeration.Niche;
import com.affiliateLink.offer.mapper.OfferMapper;
import com.affiliateLink.offer.repository.OfferRepository;
import com.affiliateLink.offer.repository.OfferSpecifications;
import com.affiliateLink.offer.repository.TagRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class OfferService {

    private static final Logger log = LoggerFactory.getLogger(OfferService.class);

    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of("id", "title", "niche", "createdAt", "advertiserId");

    private final OfferRepository offerRepository;
    private final TagRepository tagRepository;
    private final OfferMapper offerMapper;
    private final AdvertiserChecker advertiserChecker;

    public OfferService(OfferRepository offerRepository,
                        TagRepository tagRepository,
                        OfferMapper offerMapper,
                        AdvertiserChecker advertiserChecker) {
        this.offerRepository = offerRepository;
        this.tagRepository = tagRepository;
        this.offerMapper = offerMapper;
        this.advertiserChecker = advertiserChecker;
    }

    @Transactional
    public OfferDetailResponse createOffer(CreateOfferRequest request) {
        log.debug("Creating an offer for advertiser {}", request.advertiserId());
        advertiserChecker.checkCanPublish(request.advertiserId());

        Offer offer = offerMapper.toEntity(request);
        offer.setTags(resolveTags(request.tags()));

        Offer saved = offerRepository.save(offer);
        log.info("Offer created: id={}, advertiserId={}", saved.getId(), saved.getAdvertiserId());
        return offerMapper.toDetailResponse(saved);
    }

    @Transactional
    public OfferDetailResponse updateOffer(Long id, UpdateOfferRequest request) {
        Offer offer = findOfferOrThrow(id);

        offerMapper.updateEntity(request, offer);
        offer.setTags(resolveTags(request.tags()));

        Offer saved = offerRepository.save(offer);
        log.info("Offer updated: id={}", saved.getId());
        return offerMapper.toDetailResponse(saved);
    }

    public OfferDetailResponse getOffer(Long id) {
        return offerMapper.toDetailResponse(findOfferOrThrow(id));
    }

    public PageResponse<OfferResponse> searchOffers(Niche niche,
                                                    Long advertiserId,
                                                    String tag,
                                                    String keyword,
                                                    Pageable pageable) {
        checkSortFields(pageable);
        log.debug("Searching offers: niche={}, advertiserId={}, tag={}, keyword={}, page={}",
                niche, advertiserId, tag, keyword, pageable);

        Specification<Offer> spec = Specification.unrestricted();

        if (niche != null) {
            spec = spec.and(OfferSpecifications.hasNiche(niche));
        }
        if (advertiserId != null) {
            spec = spec.and(OfferSpecifications.hasAdvertiser(advertiserId));
        }
        if (tag != null && !tag.isBlank()) {
            spec = spec.and(OfferSpecifications.hasTag(tag));
        }
        if (keyword != null && !keyword.isBlank()) {
            spec = spec.and(OfferSpecifications.titleContains(keyword));
        }

        Page<OfferResponse> page = offerRepository.findAll(spec, pageable)
                .map(offerMapper::toResponse);

        return PageResponse.from(page);
    }

    private Offer findOfferOrThrow(Long id) {
        return offerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Offer not found with id: " + id));
    }

    private Set<Tag> resolveTags(Set<String> names) {
        Set<Tag> result = new HashSet<>();
        if (names == null) {
            return result;
        }
        for (String rawName : names) {
            String name = rawName.trim().toLowerCase();
            Tag tag = tagRepository.findByName(name)
                    .orElseGet(() -> {
                        log.debug("Creating new tag '{}'", name);
                        return tagRepository.save(new Tag(name));
                    });
            result.add(tag);
        }
        return result;
    }

    private void checkSortFields(Pageable pageable) {
        for (Sort.Order order : pageable.getSort()) {
            if (!ALLOWED_SORT_FIELDS.contains(order.getProperty())) {
                throw new BadRequestException("Cannot sort by '" + order.getProperty()
                        + "'. Allowed: " + ALLOWED_SORT_FIELDS);
            }
        }
    }
}
