package com.affiliateLink.profile.internal.controller;

import com.affiliateLink.profile.internal.dto.ChangeProfileStatusRequest;
import com.affiliateLink.profile.internal.dto.ProfileStatusResponse;
import com.affiliateLink.profile.internal.service.ProfileService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profiles")
public class ProfileController {

    private static final Logger log = LoggerFactory.getLogger(ProfileController.class);

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PatchMapping("/{id}/status")
    public ProfileStatusResponse changeStatus(
            @PathVariable Long id,
            @Valid @RequestBody ChangeProfileStatusRequest request) {
        log.debug("PATCH /api/profiles/{}/status", id);
        return profileService.changeStatus(id, request.newStatus());
    }
}
