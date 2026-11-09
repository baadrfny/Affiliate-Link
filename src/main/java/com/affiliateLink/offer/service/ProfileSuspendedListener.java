package com.affiliateLink.offer.service;

import com.affiliateLink.profile.api.ProfileSuspended;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class ProfileSuspendedListener {

    private static final Logger log = LoggerFactory.getLogger(ProfileSuspendedListener.class);

    private final CampaignService campaignService;
    private final PublisherNotificationService notificationService;

    ProfileSuspendedListener(CampaignService campaignService,
                             PublisherNotificationService notificationService) {
        this.campaignService = campaignService;
        this.notificationService = notificationService;
    }

    @ApplicationModuleListener
    void onProfileSuspended(ProfileSuspended event) {
        log.info("ProfileSuspended event received for profile {}", event.profileId());

        List<String> pausedOfferTitles =
                campaignService.pauseActiveCampaignsOfAdvertiser(event.profileId());

        if (pausedOfferTitles.isEmpty()) {
            log.debug("Profile {} had no active campaign: nobody to notify", event.profileId());
            return;
        }

        notificationService.notifyCampaignsPaused(event.profileId(), pausedOfferTitles);
    }
}
