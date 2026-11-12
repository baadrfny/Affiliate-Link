package com.affiliateLink;

import com.affiliateLink.profile.internal.entity.enumStatus.Niche;

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

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import java.time.LocalDate;
import com.jayway.jsonpath.JsonPath;

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
                """.formatted(Niche.values()[0].name(), email);
    }

    // 1. Publisher registration
    @Test
    void shouldRegisterPublisherSuccessfully() throws Exception {

        // Given
        String email = UUID.randomUUID() + "@example.com";

        // When & Then
        mockMvc.perform(post(PUBLISHERS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(publisherRequest(email)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.firstName").value("Ahmed"))
                .andExpect(jsonPath("$.lastName").value("Amrani"))
                .andExpect(jsonPath("$.email").value(email));

        // Verify publisher can be retrieved
        mockMvc.perform(get(PUBLISHERS_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath(
                        "$[?(@.email == '" + email + "')]"
                ).isNotEmpty());
    }

    // 2. Duplicate email
    @Test
    void shouldRejectDuplicateEmail() throws Exception {

        // Given
        String email = UUID.randomUUID() + "@example.com";

        String request = publisherRequest(email);

        // Register first publisher
        mockMvc.perform(post(PUBLISHERS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated());

        // Register another publisher with the same email
        mockMvc.perform(post(PUBLISHERS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"));
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

        String response = mockMvc.perform(post("/api/advertisers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long advertiserId = JsonPath.parse(response)
                .read("$.id", Long.class);

        mockMvc.perform(patch("/api/profiles/{id}/status", advertiserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "newStatus": "ACTIVE"
                            }
                            """))
                .andExpect(status().isOk());

        return advertiserId;
    }


    private Long createOffer(Long advertiserId) throws Exception {

        String request = """
            {
                "advertiserId": %d,
                "title": "Test Offer",
                "description": "Integration test offer",
                "landingUrl": "https://example.com",
                "niche": "%s",
                "tags": ["technology"]
            }
            """.formatted(
                advertiserId,
                com.affiliateLink.offer.entity.enumeration.Niche
                        .values()[0].name()
        );

        String response = mockMvc.perform(post("/api/offres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return JsonPath.parse(response)
                .read("$.id", Long.class);
    }


    private Long createCampaign(Long offerId) throws Exception {

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
            String status
    ) throws Exception {

        String request = """
            {
                "status": "%s"
            }
            """.formatted(status);

        mockMvc.perform(
                        patch("/api/campaigns/{id}/status", campaignId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request))
                .andExpect(
                        org.springframework.test.web.servlet.result
                                .MockMvcResultMatchers.status().isNoContent()
                );
    }


    private void assertCampaignStatus(
            Long campaignId,
            String campaignStatus
    ) throws Exception {

        mockMvc.perform(get("/api/campaigns")
                        .param("status", campaignStatus))
                .andExpect(status().isOk())
                .andExpect(jsonPath(
                        "$[?(@.id == " + campaignId + ")].status"
                ).value(
                        org.hamcrest.Matchers.contains(campaignStatus)
                ));
    }


    @Test
    void shouldCompleteCampaignLifecycle() throws Exception {

        // Given
        Long advertiserId = createActiveAdvertiser();
        Long offerId = createOffer(advertiserId);
        Long campaignId = createCampaign(offerId);

        // Verify initial status
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
}