package com.affiliateLink.offer.service;

import com.affiliateLink.common.ConflictException;
import com.affiliateLink.common.ResourceNotFoundException;
import com.affiliateLink.profile.ProfileApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
class AdvertiserChecker {

    private static final Logger log = LoggerFactory.getLogger(AdvertiserChecker.class);

    private final ProfileApi profileApi;

    AdvertiserChecker(ProfileApi profileApi) {
        this.profileApi = profileApi;
    }

    /** 404 if the advertiser does not exist, 409 if it is not ACTIVE */
    void checkCanPublish(Long advertiserId) {
        if (!profileApi.advertiserExists(advertiserId)) {
            throw new ResourceNotFoundException("Advertiser not found with id: " + advertiserId);
        }
        if (!profileApi.isAdvertiserActive(advertiserId)) {
            log.warn("Advertiser {} is not active: publication refused", advertiserId);
            throw new ConflictException("Advertiser account is not active");
        }
    }
}
