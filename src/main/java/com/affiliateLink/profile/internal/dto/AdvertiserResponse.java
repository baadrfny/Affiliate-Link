package com.affiliateLink.profile.internal.dto;


import com.affiliateLink.profile.internal.entity.enumStatus.status;
import java.time.LocalDateTime;

public record AdvertiserResponse(
        Long id,
        String companyName,
        String sector,
        String email,
        status statu,
        String website,
        LocalDateTime createdAt
) {}