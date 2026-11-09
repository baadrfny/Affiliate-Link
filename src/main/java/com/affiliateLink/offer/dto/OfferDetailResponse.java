package com.affiliateLink.offer.dto;

import com.affiliateLink.offer.entity.enumeration.Niche;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record OfferDetailResponse(
        Long id,
        Long advertiserId,
        String title,
        String description,
        String landingUrl,
        Niche niche,
        Set<String> tags,
        LocalDateTime createdAt,
        List<CampaignResponse> campaigns
) {
}
