package com.affiliateLink.profile.controller;

import com.affiliateLink.profile.dto.ChangeProfileStatusRequest;
import com.affiliateLink.profile.entity.Profile;
import com.affiliateLink.profile.service.ProfileService;
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