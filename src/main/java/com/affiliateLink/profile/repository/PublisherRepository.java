package com.affiliateLink.profile.repository;

import com.affiliateLink.profile.entity.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublisherRepository extends JpaRepository<Publisher , Long> {


}
