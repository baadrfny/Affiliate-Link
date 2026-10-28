package com.affiliateLink.profile.internal.service;


import com.affiliateLink.common.ResourceNotFoundException;
import com.affiliateLink.profile.internal.entity.Profile;
import com.affiliateLink.profile.internal.repository.ProfileRepository;
import org.springframework.stereotype.Service;
import com.affiliateLink.profile.internal.entity.enumStatus.status;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;

    public ProfileService(ProfileRepository profileRepository){
        this.profileRepository = profileRepository;
    }


    public Profile getProfileById(Long id){
        return profileRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Profile not found"));
    }


    public Profile changeStatus(Long id, status newStatus) {
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Profile not found"));

        if (profile.getStatu() == status.PENDING
                && newStatus == status.ACTIVE.ACTIVE) {
            profile.setStatu(status.ACTIVE);
        } else if (profile.getStatu() == status.ACTIVE
                && newStatus == status.SUSPENDED) {
            profile.setStatu(status.SUSPENDED);
        } else if (profile.getStatu() == status.SUSPENDED
                && newStatus == status.ACTIVE) {
            profile.setStatu(status.ACTIVE);
        } else {
            throw new IllegalArgumentException("Invalid status transition");
        }

        return profileRepository.save(profile);
    }


}
