package com.affiliateLink.offer.dto;

import com.affiliateLink.offer.entity.enumeration.CampaignStatus;
import jakarta.validation.constraints.NotNull;

public record ChangeCampaignStatusRequest(

        @NotNull(message = "Status is required")
        CampaignStatus status
) {
}
