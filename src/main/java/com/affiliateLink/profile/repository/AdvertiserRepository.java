package com.affiliateLink.profile.repository;

import com.affiliateLink.profile.entity.Advertiser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdvertiserRepository extends JpaRepository<Advertiser, Long> {

}