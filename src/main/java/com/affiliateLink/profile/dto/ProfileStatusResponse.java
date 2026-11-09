package com.affiliateLink.profile.dto;

import com.affiliateLink.profile.entity.enumStatus.status;

import java.time.LocalDateTime;

public record ProfileStatusResponse(
        Long id,
        String email,
        status statu,
        LocalDateTime createdAt
) {
}
