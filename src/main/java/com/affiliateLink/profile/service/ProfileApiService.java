package com.affiliateLink.profile.service;

import com.affiliateLink.profile.ProfileApi;
import com.affiliateLink.profile.PublisherContact;
import com.affiliateLink.profile.entity.Publisher;
import com.affiliateLink.profile.entity.enumStatus.status;
import com.affiliateLink.profile.mapper.PublisherMapper;
import com.affiliateLink.profile.repository.AdvertiserRepository;
import com.affiliateLink.profile.repository.PublisherRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
class ProfileApiService implements ProfileApi {

    private static final Logger log = LoggerFactory.getLogger(ProfileApiService.class);

    private final AdvertiserRepository advertiserRepository;
    private final PublisherRepository publisherRepository;
    private final PublisherMapper publisherMapper;

    ProfileApiService(AdvertiserRepository advertiserRepository,
                      PublisherRepository publisherRepository,
                      PublisherMapper publisherMapper) {
        this.advertiserRepository = advertiserRepository;
        this.publisherRepository = publisherRepository;
        this.publisherMapper = publisherMapper;
    }

    @Override
    public boolean advertiserExists(Long advertiserId) {
        log.debug("ProfileApi: does advertiser {} exist?", advertiserId);
        return advertiserRepository.existsById(advertiserId);
    }

    @Override
    public boolean isAdvertiserActive(Long advertiserId) {
        log.debug("ProfileApi: is advertiser {} active?", advertiserId);
        return advertiserRepository.findById(advertiserId)
                .map(advertiser -> advertiser.getStatu() == status.ACTIVE)
                .orElse(false);
    }

    @Override
    public List<PublisherContact> findActivePublishers() {
        List<Publisher> activePublishers = publisherRepository.findByStatu(status.ACTIVE);
        log.debug("ProfileApi: {} active publisher(s) found", activePublishers.size());
        return publisherMapper.toContacts(activePublishers);
    }
}
