package com.affiliateLink.offer.repository;

import com.affiliateLink.offer.entity.Campaign;
import com.affiliateLink.offer.entity.enumeration.CampaignStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    List<Campaign> findByStatus(CampaignStatus status);

    List<Campaign> findByOfferIdAndStatus(Long offerId, CampaignStatus status);

    @Query("select c from Campaign c join fetch c.offer o "
            + "where o.advertiserId = :advertiserId and c.status = :status")
    List<Campaign> findByAdvertiserIdAndStatus(@Param("advertiserId") Long advertiserId,
                                               @Param("status") CampaignStatus status);
}
