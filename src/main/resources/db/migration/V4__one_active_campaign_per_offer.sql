CREATE UNIQUE INDEX uq_offer_campaigns_one_active_per_offer
    ON offer_campaigns (offer_id)
    WHERE status = 'ACTIVE';
