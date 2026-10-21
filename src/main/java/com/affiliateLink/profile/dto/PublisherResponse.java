package com.affiliateLink.profile.dto;

import com.affiliateLink.profile.entity.enumStatus.Niche;
import com.affiliateLink.profile.entity.enumStatus.status;


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

