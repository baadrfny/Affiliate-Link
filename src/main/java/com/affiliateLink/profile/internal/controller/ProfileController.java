package com.affiliateLink.profile.internal.controller;

import com.affiliateLink.profile.internal.dto.ChangeProfileStatusRequest;
import com.affiliateLink.profile.internal.entity.Profile;
import com.affiliateLink.profile.internal.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profiles")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PatchMapping("/{id}/status")
    public Profile changeStatus(
            @PathVariable Long id,
            @Valid @RequestBody ChangeProfileStatusRequest request) {

        return profileService.changeStatus(id, request.getNewStatus());
    }
}