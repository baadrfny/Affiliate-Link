package com.affiliateLink.profile.internal.dto;

import com.affiliateLink.profile.internal.entity.enumStatus.status;

import java.time.LocalDateTime;

public record ProfileStatusResponse(
        Long id,
        String email,
        status statu,
        LocalDateTime createdAt
) {
}
