package com.affiliateLink.profile.internal.dto;

import com.affiliateLink.profile.internal.entity.enumStatus.Niche;
import com.affiliateLink.profile.internal.entity.enumStatus.status;


import java.time.LocalDateTime;

public record PublisherResponse(

        Long id,
        String firstName,
        String lastName,
        String email,
        status statu,
        Niche niche,
        String bio,
        String country,
        LocalDateTime createdAt

        ){}

