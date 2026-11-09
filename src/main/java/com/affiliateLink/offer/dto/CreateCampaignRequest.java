package com.affiliateLink.offer.dto;

import com.affiliateLink.offer.entity.enumeration.PayoutModel;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateCampaignRequest(

        @NotBlank(message = "Name cannot be blank")
        @Size(max = 255, message = "Name cannot exceed 255 characters")
        String name,

        @NotNull(message = "Start date is required")
        LocalDate startDate,

        @NotNull(message = "End date is required")
        LocalDate endDate,

        @NotNull(message = "Budget is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Budget must be greater than 0")
        @Digits(integer = 13, fraction = 2, message = "Invalid budget format")
        BigDecimal budget,

        @NotNull(message = "Payout model is required")
        PayoutModel payoutModel,

        @NotNull(message = "Payout value is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Payout value must be greater than 0")
        @Digits(integer = 13, fraction = 2, message = "Invalid payout value format")
        BigDecimal payoutValue
) {
}
