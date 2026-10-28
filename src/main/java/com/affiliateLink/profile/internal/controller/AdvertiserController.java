package com.affiliateLink.profile.internal.controller;

import com.affiliateLink.profile.internal.dto.AdvertiserRequest;
import com.affiliateLink.profile.internal.dto.AdvertiserResponse;
import com.affiliateLink.profile.internal.entity.Advertiser;
import com.affiliateLink.profile.internal.service.AdvertiserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/advertisers")
public class AdvertiserController {

    private final AdvertiserService advertiserService;

    public AdvertiserController(AdvertiserService advertiserService) {
        this.advertiserService = advertiserService;
    }

    @PostMapping
    public ResponseEntity<AdvertiserResponse> addAdvertiser(
            @Valid @RequestBody AdvertiserRequest request) {

        return new ResponseEntity<>(
                advertiserService.createAdvertiser(request),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<List<Advertiser>> getAllAdvertisers() {
        return ResponseEntity.ok(
                advertiserService.getAllAdvertisers()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Advertiser> getAdvertiser(@PathVariable Long id) {
        return ResponseEntity.ok(
                advertiserService.getAdvertiserById(id)
        );
    }

}