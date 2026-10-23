package com.affiliateLink.profile.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdvertiserRequest(

        @NotBlank(message = "Company name cannot be blank")
        @Size(max = 255, message = "Company name cannot exceed 255 characters")
        String companyName,

        @NotBlank(message = "Sector cannot be blank")
        @Size(max = 100, message = "Sector cannot exceed 100 characters")
        String sector,

        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Invalid email format")
        String email,

        String website

) {}