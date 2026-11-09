package com.affiliateLink.profile.repository;

import com.affiliateLink.profile.entity.Publisher;
import com.affiliateLink.profile.entity.enumStatus.status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PublisherRepository extends JpaRepository<Publisher, Long> {

    List<Publisher> findByStatu(status statu);
}
