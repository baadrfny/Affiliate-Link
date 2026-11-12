package com.affiliateLink;

import com.affiliateLink.profile.internal.entity.enumStatus.Niche;
import com.jayway.jsonpath.JsonPath;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.time.LocalDate;
import java.time.Duration;
import java.util.UUID;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.contains;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class AffiliateLinkIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:16-alpine");

    @Autowired
    private MockMvc mockMvc;

    private static final String PUBLISHERS_URL = "/api/publishers";
    private static final String ADVERTISERS_URL = "/api/advertisers";
    private static final String OFFERS_URL = "/api/offres";
    private static final String CAMPAIGNS_URL = "/api/campaigns";

    // ==========================================
    // HELPER METHODS
    // ==========================================

    private String publisherRequest(String email) {

        return """
                {
                    "firstName": "Ahmed",
                    "lastName": "Amrani",
                    "niche": "%s",
                    "email": "%s",
                    "bio": "Test publisher",
                    "country": "Morocco"
                }
                """.formatted(
                Niche.values()[0].name(),
                email
        );
    }

    private Long createActiveAdvertiser() throws Exception {

        String email = UUID.randomUUID() + "@example.com";

        String request = """
                {
                    "companyName": "Test Company",
                    "sector": "Technology",
                    "email": "%s",
                    "website": "https://example.com"
                }
                """.formatted(email);

        String response = mockMvc.perform(
                        post(ADVERTISERS_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long advertiserId = JsonPath.parse(response)
                .read("$.id", Long.class);

        changeProfileStatus(advertiserId, "ACTIVE");

        return advertiserId;
    }

    private void changeProfileStatus(
            Long profileId,
            String newStatus
    ) throws Exception {

        String request = """
                {
                    "newStatus": "%s"
                }
                """.formatted(newStatus);

        mockMvc.perform(
                        patch("/api/profiles/{id}/status", profileId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request))
                .andExpect(status().isOk());
    }

    private Long createOffer(Long advertiserId)
            throws Exception {

        return createOffer(advertiserId, "Test Offer");
    }

    private Long createOffer(
            Long advertiserId,
            String title
    ) throws Exception {

        String request = """
                {
                    "advertiserId": %d,
                    "title": "%s",
                    "description": "Integration test offer",
                    "landingUrl": "https://example.com",
                    "niche": "%s",
                    "tags": ["technology"]
                }
                """.formatted(
                advertiserId,
                title,
                com.affiliateLink.offer.entity.enumeration.Niche
                        .values()[0].name()
        );

        String response = mockMvc.perform(
                        post(OFFERS_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return JsonPath.parse(response)
                .read("$.id", Long.class);
    }

    private Long createCampaign(Long offerId)
            throws Exception {

        String request = """
                {
                    "name": "Test Campaign",
                    "startDate": "%s",
                    "endDate": "%s",
                    "budget": 1000,
                    "payoutModel": "CPA_FIXED",
                    "payoutValue": 10
                }
                """.formatted(
                LocalDate.now(),
                LocalDate.now().plusDays(30)
        );

        String response = mockMvc.perform(
                        post("/api/offres/{offerId}/campaigns", offerId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return JsonPath.parse(response)
                .read("$.id", Long.class);
    }

    private void changeCampaignStatus(
            Long campaignId,
            String newStatus
    ) throws Exception {

        String request = """
                {
                    "status": "%s"
                }
                """.formatted(newStatus);

        mockMvc.perform(
                        patch("/api/campaigns/{id}/status", campaignId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request))
                .andExpect(status().isNoContent());
    }

    private void assertCampaignStatus(
            Long campaignId,
            String campaignStatus
    ) throws Exception {

        mockMvc.perform(
                        get(CAMPAIGNS_URL)
                                .param("status", campaignStatus))
                .andExpect(status().isOk())
                .andExpect(jsonPath(
                        "$[?(@.id == " + campaignId + ")].status"
                ).value(contains(campaignStatus)));
    }

    // ==========================================
    // TEST 1 — PUBLISHER REGISTRATION
    // ==========================================

    @Test
    void shouldRegisterPublisherSuccessfully()
            throws Exception {

        // Given
        String email = UUID.randomUUID() + "@example.com";

        // When & Then
        mockMvc.perform(
                        post(PUBLISHERS_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(publisherRequest(email)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.firstName").value("Ahmed"))
                .andExpect(jsonPath("$.lastName").value("Amrani"))
                .andExpect(jsonPath("$.email").value(email));

        // Verify publisher exists
        mockMvc.perform(get(PUBLISHERS_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath(
                        "$[?(@.email == '" + email + "')]"
                ).isNotEmpty());
    }

    // ==========================================
    // TEST 2 — DUPLICATE EMAIL
    // ==========================================

    @Test
    void shouldRejectDuplicateEmail()
            throws Exception {

        // Given
        String email = UUID.randomUUID() + "@example.com";
        String request = publisherRequest(email);

        // Register first publisher
        mockMvc.perform(
                        post(PUBLISHERS_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request))
                .andExpect(status().isCreated());

        // Try registering the same email
        mockMvc.perform(
                        post(PUBLISHERS_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"));
    }

    // ==========================================
    // TEST 3 — CAMPAIGN LIFECYCLE
    // ==========================================

    @Test
    void shouldCompleteCampaignLifecycle()
            throws Exception {

        // Given
        Long advertiserId = createActiveAdvertiser();
        Long offerId = createOffer(advertiserId);
        Long campaignId = createCampaign(offerId);

        // DRAFT
        assertCampaignStatus(campaignId, "DRAFT");

        // DRAFT -> ACTIVE
        changeCampaignStatus(campaignId, "ACTIVE");
        assertCampaignStatus(campaignId, "ACTIVE");

        // ACTIVE -> PAUSED
        changeCampaignStatus(campaignId, "PAUSED");
        assertCampaignStatus(campaignId, "PAUSED");

        // PAUSED -> ACTIVE
        changeCampaignStatus(campaignId, "ACTIVE");
        assertCampaignStatus(campaignId, "ACTIVE");

        // ACTIVE -> ENDED
        changeCampaignStatus(campaignId, "ENDED");
        assertCampaignStatus(campaignId, "ENDED");
    }

    // ==========================================
    // TEST 4 — OFFER SEARCH
    // ==========================================

    @Test
    void shouldSearchOffersWithFiltersAndPagination()
            throws Exception {

        // Given
        Long advertiserId = createActiveAdvertiser();

        createOffer(advertiserId, "Java Course");
        createOffer(advertiserId, "Angular Course");

        // Offer that should be excluded by keyword
        createOffer(advertiserId, "Spring Boot Training");

        // Offer that should be excluded by advertiser
        Long otherAdvertiserId = createActiveAdvertiser();
        createOffer(otherAdvertiserId, "Python Course");

        // First page
        mockMvc.perform(
                        get(OFFERS_URL)
                                .param("advertiserId",
                                        advertiserId.toString())
                                .param("keyword", "Course")
                                .param("page", "0")
                                .param("size", "1")
                                .param("sort", "title,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].title")
                        .value("Angular Course"));

        // Second page
        mockMvc.perform(
                        get(OFFERS_URL)
                                .param("advertiserId",
                                        advertiserId.toString())
                                .param("keyword", "Course")
                                .param("page", "1")
                                .param("size", "1")
                                .param("sort", "title,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].title")
                        .value("Java Course"));
    }

    // ==========================================
    // TEST 5 — ADVERTISER SUSPENSION
    // ==========================================

    @Test
    void shouldPauseCampaignWhenAdvertiserIsSuspended()
            throws Exception {

        // Given
        Long advertiserId = createActiveAdvertiser();
        Long offerId = createOffer(advertiserId);
        Long campaignId = createCampaign(offerId);

        // Activate campaign
        changeCampaignStatus(campaignId, "ACTIVE");
        assertCampaignStatus(campaignId, "ACTIVE");

        // Create another advertiser and active campaign
        Long otherAdvertiserId = createActiveAdvertiser();
        Long otherOfferId = createOffer(otherAdvertiserId);
        Long otherCampaignId = createCampaign(otherOfferId);

        changeCampaignStatus(otherCampaignId, "ACTIVE");

        // When advertiser is suspended
        changeProfileStatus(advertiserId, "SUSPENDED");

        // Then wait for the asynchronous Modulith event
        await()
                .atMost(Duration.ofSeconds(10))
                .untilAsserted(() ->
                        assertCampaignStatus(campaignId, "PAUSED")
                );

        // Verify the other advertiser is not affected
        assertCampaignStatus(otherCampaignId, "ACTIVE");

        // Verify suspended advertiser cannot publish offers
        String request = """
                {
                    "advertiserId": %d,
                    "title": "Rejected Offer",
                    "description": "Suspended advertiser test",
                    "landingUrl": "https://example.com",
                    "niche": "%s",
                    "tags": ["technology"]
                }
                """.formatted(
                advertiserId,
                com.affiliateLink.offer.entity.enumeration.Niche
                        .values()[0].name()
        );

        mockMvc.perform(
                        post(OFFERS_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request))
                .andExpect(status().isConflict());
    }
}