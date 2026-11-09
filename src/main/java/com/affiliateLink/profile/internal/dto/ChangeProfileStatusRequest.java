package com.affiliateLink.profile.internal.dto;

import com.affiliateLink.profile.internal.entity.enumStatus.status;
import jakarta.validation.constraints.NotNull;

public record ChangeProfileStatusRequest(

        @NotNull(message = "New status is required")
        status newStatus
) {
}
