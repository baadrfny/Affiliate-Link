package com.affiliateLink.offer.controller;

import com.affiliateLink.common.PageResponse;
import com.affiliateLink.offer.dto.CreateOfferRequest;
import com.affiliateLink.offer.dto.OfferDetailResponse;
import com.affiliateLink.offer.dto.OfferResponse;
import com.affiliateLink.offer.dto.UpdateOfferRequest;
import com.affiliateLink.offer.entity.enumeration.Niche;
import com.affiliateLink.offer.service.OfferService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/offres")
public class OfferController {

    private static final Logger log = LoggerFactory.getLogger(OfferController.class);

    private final OfferService offerService;

    public OfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    @PostMapping
    public ResponseEntity<OfferDetailResponse> createOffer(
            @Valid @RequestBody CreateOfferRequest request) {
        log.debug("POST /api/offres");
        return new ResponseEntity<>(offerService.createOffer(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<PageResponse<OfferResponse>> searchOffers(
            @RequestParam(required = false) Niche niche,
            @RequestParam(required = false) Long advertiserId,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        log.debug("GET /api/offres");
        return ResponseEntity.ok(
                offerService.searchOffers(niche, advertiserId, tag, keyword, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OfferDetailResponse> getOffer(@PathVariable Long id) {
        log.debug("GET /api/offres/{}", id);
        return ResponseEntity.ok(offerService.getOffer(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OfferDetailResponse> updateOffer(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOfferRequest request) {
        log.debug("PUT /api/offres/{}", id);
        return ResponseEntity.ok(offerService.updateOffer(id, request));
    }
}
