package com.affiliateLink.profile.api;

import com.affiliateLink.profile.internal.entity.enumStatus.status;
import com.affiliateLink.profile.internal.repository.AdvertiserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileApi {

    private final AdvertiserRepository advertiserRepository;

    @Transactional(readOnly = true)
    public boolean isAdvertiserActive(Long advertiserId) {
        return advertiserRepository.existsByIdAndStatu(
                advertiserId,
                status.ACTIVE
        );
    }
}