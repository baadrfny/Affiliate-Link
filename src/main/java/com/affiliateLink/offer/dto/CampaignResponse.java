package com.affiliateLink.offer.dto;

import com.affiliateLink.offer.entity.enumeration.CampaignStatus;
import com.affiliateLink.offer.entity.enumeration.PayoutModel;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CampaignResponse(
        Long id,
        String name,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal budget,
        PayoutModel payoutModel,
        BigDecimal payoutValue,
        CampaignStatus status
) {
}
