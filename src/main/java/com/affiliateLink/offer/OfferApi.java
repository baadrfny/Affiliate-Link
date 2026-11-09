package com.affiliateLink.offer;

import java.util.Optional;

/**
 * Public API of the offer module (will be used by the future affiliate module).
 */
public interface OfferApi {

    Optional<CampaignInfo> getCampaign(Long campaignId);
}
