package com.affiliateLink.offer.dto;

import com.affiliateLink.offer.entity.enumeration.Niche;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record CreateOfferRequest(

        @NotNull(message = "Advertiser id is required")
        Long advertiserId,

        @NotBlank(message = "Title cannot be blank")
        @Size(max = 255, message = "Title cannot exceed 255 characters")
        String title,

        @Size(max = 5000, message = "Description cannot exceed 5000 characters")
        String description,

        @NotBlank(message = "Landing URL cannot be blank")
        @Size(max = 500, message = "Landing URL cannot exceed 500 characters")
        @Pattern(regexp = "^https?://\\S+$", message = "Landing URL must start with http:// or https://")
        String landingUrl,

        @NotNull(message = "Niche is required")
        Niche niche,

        Set<@NotBlank(message = "A tag cannot be blank")
            @Size(max = 100, message = "A tag cannot exceed 100 characters") String> tags
) {
}
