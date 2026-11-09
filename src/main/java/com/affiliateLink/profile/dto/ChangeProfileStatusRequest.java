package com.affiliateLink.profile.dto;

import com.affiliateLink.profile.entity.enumStatus.status;
import jakarta.validation.constraints.NotNull;

public record ChangeProfileStatusRequest(

        @NotNull(message = "New status is required")
        status newStatus
) {
}
