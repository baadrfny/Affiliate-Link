package com.affiliateLink.profile.internal.repository;

import com.affiliateLink.profile.internal.entity.Advertiser;
import com.affiliateLink.profile.internal.entity.enumStatus.status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdvertiserRepository extends JpaRepository<Advertiser, Long> {
    boolean existsByIdAndStatu(Long id, status statu);
}