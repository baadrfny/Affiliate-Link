package com.affiliateLink.offer.controller;

import com.affiliateLink.offer.dto.CampaignResponse;
import com.affiliateLink.offer.dto.ChangeCampaignStatusRequest;
import com.affiliateLink.offer.dto.CreateCampaignRequest;
import com.affiliateLink.offer.entity.enumeration.CampaignStatus;
import com.affiliateLink.offer.service.CampaignService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CampaignController {

    private static final Logger log = LoggerFactory.getLogger(CampaignController.class);

    private final CampaignService campaignService;

    public CampaignController(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    @PostMapping("/api/offres/{offerId}/campaigns")
    public ResponseEntity<CampaignResponse> createCampaign(
            @PathVariable Long offerId,
            @Valid @RequestBody CreateCampaignRequest request) {
        log.debug("POST /api/offres/{}/campaigns", offerId);
        return new ResponseEntity<>(
                campaignService.createCampaign(offerId, request), HttpStatus.CREATED);
    }

    @GetMapping("/api/campaigns")
    public ResponseEntity<List<CampaignResponse>> getCampaigns(
            @RequestParam(defaultValue = "ACTIVE") CampaignStatus status) {
        log.debug("GET /api/campaigns?status={}", status);
        return ResponseEntity.ok(campaignService.getCampaigns(status));
    }

    @PatchMapping("/api/campaigns/{id}/status")
    public ResponseEntity<Void> changeStatus(
            @PathVariable Long id,
            @Valid @RequestBody ChangeCampaignStatusRequest request) {
        log.debug("PATCH /api/campaigns/{}/status", id);
        campaignService.changeStatus(id, request.status());
        return ResponseEntity.noContent().build();
    }
}
