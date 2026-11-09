package com.affiliateLink.profile.internal.dto;

import com.affiliateLink.profile.internal.entity.enumStatus.Niche;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;

public record PublisherRequest(
        @NotBlank(message = "First name cannot be blank")
        @Size(min = 3 , max = 20 , message = "First name must be between 3 and 20 ")
        String firstName,

        @NotBlank(message = "Last name cannot be blank")
        @Size(min = 3 , max = 20 , message = "Last name must be between 3 and 20 ")
        String lastName,

        @NotNull(message = "You must choice a niche")
        Niche niche,

        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Invalid email format")
        String email,

        String bio,
        String country


){};

