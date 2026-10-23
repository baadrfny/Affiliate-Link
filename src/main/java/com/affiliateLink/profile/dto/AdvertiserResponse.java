package com.affiliateLink.profile.dto;


import com.affiliateLink.profile.entity.enumStatus.status;
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