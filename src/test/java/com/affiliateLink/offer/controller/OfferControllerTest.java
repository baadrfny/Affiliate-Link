package com.affiliateLink.offer.controller;

import com.affiliateLink.common.ConflictException;
import com.affiliateLink.common.ResourceNotFoundException;
import com.affiliateLink.offer.dto.CreateOfferRequest;
import com.affiliateLink.offer.dto.OfferDetailResponse;
import com.affiliateLink.offer.entity.enumeration.Niche;
import com.affiliateLink.offer.service.OfferService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OfferController.class)
class OfferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OfferService offerService;

    private static final String BASE_URL = "/api/offres";

    private String validRequest() {
        return """
                {
                    "advertiserId": 1,
                    "title": "Test Offer",
                    "description": "Test description",
                    "landingUrl": "https://example.com",
                    "niche": "%s",
                    "tags": ["test"]
                }
                """.formatted(Niche.values()[0].name());
    }

    // HTTP 201 - Created
    @Test
    void shouldCreateOfferAndReturn201() throws Exception {

        // Given
        OfferDetailResponse response =
                mock(OfferDetailResponse.class);

        when(offerService.createOffer(any(CreateOfferRequest.class)))
                .thenReturn(response);

        // When & Then
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequest()))
                .andExpect(status().isCreated());

        verify(offerService)
                .createOffer(any(CreateOfferRequest.class));
    }

    // HTTP 400 - Bad Request
    @Test
    void shouldReturn400WhenRequestIsInvalid() throws Exception {

        // Given
        String request = """
                {
                    "title": "",
                    "landingUrl": ""
                }
                """;

        // When & Then
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message")
                        .value("Invalid request data"))
                .andExpect(jsonPath("$.path").value(BASE_URL));

        verify(offerService, never())
                .createOffer(any(CreateOfferRequest.class));
    }

    // HTTP 404 - Not Found
    @Test
    void shouldReturn404WhenOfferNotFound() throws Exception {

        // Given
        when(offerService.getOffer(99L))
                .thenThrow(new ResourceNotFoundException(
                        "Offer not found"
                ));

        // When & Then
        mockMvc.perform(get(BASE_URL + "/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message")
                        .value("Offer not found"))
                .andExpect(jsonPath("$.path")
                        .value(BASE_URL + "/99"));

        verify(offerService).getOffer(99L);
    }

    // HTTP 409 - Conflict
    @Test
    void shouldReturn409WhenAdvertiserCannotPublish()
            throws Exception {

        // Given
        when(offerService.createOffer(any(CreateOfferRequest.class)))
                .thenThrow(new ConflictException(
                        "Advertiser cannot publish"
                ));

        // When & Then
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequest()))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message")
                        .value("Advertiser cannot publish"))
                .andExpect(jsonPath("$.path").value(BASE_URL));
    }
}