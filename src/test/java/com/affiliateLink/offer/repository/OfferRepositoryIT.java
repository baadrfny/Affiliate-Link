package com.affiliateLink.offer.repository;

import com.affiliateLink.offer.entity.Offer;
import com.affiliateLink.offer.entity.enumeration.Niche;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;

import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
class OfferRepositoryIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:16-alpine");

    @Autowired
    private OfferRepository offerRepository;

    @Test
    void shouldSaveAndFindOffer() {

        // Given
        Offer offer = new Offer();
        offer.setAdvertiserId(1L);
        offer.setTitle("Test Offer");
        offer.setLandingUrl("https://example.com");
        offer.setNiche(Niche.values()[0]);

        // When
        Offer saved = offerRepository.saveAndFlush(offer);

        Offer found = offerRepository.findById(saved.getId())
                .orElseThrow();

        // Then
        assertThat(found.getId()).isNotNull();
        assertThat(found.getTitle()).isEqualTo("Test Offer");
        assertThat(found.getAdvertiserId()).isEqualTo(1L);
        assertThat(found.getNiche()).isEqualTo(offer.getNiche());
    }
}