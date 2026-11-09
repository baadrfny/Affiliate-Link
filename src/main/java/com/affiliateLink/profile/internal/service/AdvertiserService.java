package com.affiliateLink.profile.internal.service;

import com.affiliateLink.common.EmailAlreadyExistsException;
import com.affiliateLink.common.ResourceNotFoundException;
import com.affiliateLink.profile.internal.dto.AdvertiserRequest;
import com.affiliateLink.profile.internal.dto.AdvertiserResponse;
import com.affiliateLink.profile.internal.entity.Advertiser;
import com.affiliateLink.profile.mapper.AdvertiserMapper;
import com.affiliateLink.profile.internal.repository.AdvertiserRepository;
import com.affiliateLink.profile.internal.repository.ProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AdvertiserService {

    private static final Logger log = LoggerFactory.getLogger(AdvertiserService.class);

    private final AdvertiserRepository advertiserRepository;
    private final ProfileRepository profileRepository;
    private final AdvertiserMapper advertiserMapper;

    public AdvertiserService(AdvertiserRepository advertiserRepository,
                             ProfileRepository profileRepository,
                             AdvertiserMapper advertiserMapper) {
        this.advertiserRepository = advertiserRepository;
        this.profileRepository = profileRepository;
        this.advertiserMapper = advertiserMapper;
    }

    @Transactional
    public AdvertiserResponse createAdvertiser(AdvertiserRequest request) {

        if (profileRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        Advertiser saved = advertiserRepository.save(advertiserMapper.toEntity(request));
        log.info("Advertiser created: id={}", saved.getId());

        return advertiserMapper.toResponse(saved);
    }

    public AdvertiserResponse getAdvertiserById(Long id) {
        log.debug("Reading advertiser {}", id);
        Advertiser advertiser = advertiserRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Advertiser not found with id: " + id));
        return advertiserMapper.toResponse(advertiser);
    }

    public List<AdvertiserResponse> getAllAdvertisers() {
        log.debug("Reading all advertisers");
        return advertiserMapper.toResponses(advertiserRepository.findAll());
    }
}
