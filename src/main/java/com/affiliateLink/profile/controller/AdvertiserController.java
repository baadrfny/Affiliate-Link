package com.affiliateLink.profile.controller;

import com.affiliateLink.profile.dto.AdvertiserRequest;
import com.affiliateLink.profile.dto.AdvertiserResponse;
import com.affiliateLink.profile.service.AdvertiserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/advertisers")
public class AdvertiserController {

    private static final Logger log = LoggerFactory.getLogger(AdvertiserController.class);

    private final AdvertiserService advertiserService;

    public AdvertiserController(AdvertiserService advertiserService) {
        this.advertiserService = advertiserService;
    }

    @PostMapping
    public ResponseEntity<AdvertiserResponse> addAdvertiser(
            @Valid @RequestBody AdvertiserRequest request) {
        log.debug("POST /api/advertisers");
        return new ResponseEntity<>(
                advertiserService.createAdvertiser(request),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<List<AdvertiserResponse>> getAllAdvertisers() {
        log.debug("GET /api/advertisers");
        return ResponseEntity.ok(advertiserService.getAllAdvertisers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdvertiserResponse> getAdvertiser(@PathVariable Long id) {
        log.debug("GET /api/advertisers/{}", id);
        return ResponseEntity.ok(advertiserService.getAdvertiserById(id));
    }
}
