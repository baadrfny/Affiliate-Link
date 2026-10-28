package com.affiliateLink.profile.internal.repository;

import com.affiliateLink.profile.internal.entity.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublisherRepository extends JpaRepository<Publisher , Long> {


}
