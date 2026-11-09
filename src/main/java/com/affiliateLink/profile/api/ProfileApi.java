package com.affiliateLink.profile.api;

import java.util.List;

/**
 * Public API of the profile module.
 * Other modules (like offer) can ONLY talk to profile through this interface.
 */
public interface ProfileApi {

    /** Does an advertiser with this id exist? */
    boolean advertiserExists(Long advertiserId);

    /** Is this advertiser's account ACTIVE? (false if it does not exist) */
    boolean isAdvertiserActive(Long advertiserId);

    /** Contact info of all ACTIVE publishers (used later to send emails). */
    List<PublisherContact> findActivePublishers();
}
