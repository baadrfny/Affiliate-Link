package com.affiliateLink.profile.service;

import com.affiliateLink.common.ConflictException;
import com.affiliateLink.common.ResourceNotFoundException;
import com.affiliateLink.profile.ProfileSuspended;
import com.affiliateLink.profile.dto.ProfileStatusResponse;
import com.affiliateLink.profile.entity.Profile;
import com.affiliateLink.profile.entity.enumStatus.status;
import com.affiliateLink.profile.mapper.ProfileMapper;
import com.affiliateLink.profile.repository.ProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileService {

    private static final Logger log = LoggerFactory.getLogger(ProfileService.class);

    private final ProfileRepository profileRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ProfileMapper profileMapper;

    public ProfileService(ProfileRepository profileRepository,
                          ApplicationEventPublisher eventPublisher,
                          ProfileMapper profileMapper) {
        this.profileRepository = profileRepository;
        this.eventPublisher = eventPublisher;
        this.profileMapper = profileMapper;
    }

    @Transactional(readOnly = true)
    public Profile getProfileById(Long id) {
        return profileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
    }

    @Transactional
    public ProfileStatusResponse changeStatus(Long id, status newStatus) {
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        status oldStatus = profile.getStatu();

        if (oldStatus == status.PENDING && newStatus == status.ACTIVE) {
            profile.setStatu(status.ACTIVE);
        } else if (oldStatus == status.ACTIVE && newStatus == status.SUSPENDED) {
            profile.setStatu(status.SUSPENDED);
        } else if (oldStatus == status.SUSPENDED && newStatus == status.ACTIVE) {
            profile.setStatu(status.ACTIVE);
        } else {
            log.warn("Invalid status transition refused for profile {}: {} -> {}",
                    id, oldStatus, newStatus);
            throw new ConflictException(
                    "Invalid status transition: " + oldStatus + " -> " + newStatus);
        }

        Profile saved = profileRepository.save(profile);
        log.info("Profile {} status changed: {} -> {}", id, oldStatus, newStatus);

        if (newStatus == status.SUSPENDED) {
            eventPublisher.publishEvent(new ProfileSuspended(saved.getId()));
            log.info("ProfileSuspended event published for profile {}", id);
        }

        return profileMapper.toStatusResponse(saved);
    }
}
