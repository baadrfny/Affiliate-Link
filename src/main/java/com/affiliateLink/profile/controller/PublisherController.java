package com.affiliateLink.profile.controller;

import com.affiliateLink.profile.dto.PublisherRequest;
import com.affiliateLink.profile.dto.PublisherResponse;
import com.affiliateLink.profile.service.PublisherService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/publishers")
public class PublisherController {

    private static final Logger log = LoggerFactory.getLogger(PublisherController.class);

    private final PublisherService publisherService;

    public PublisherController(PublisherService publisherService) {
        this.publisherService = publisherService;
    }

    @PostMapping
    public ResponseEntity<PublisherResponse> addPublisher(
            @Valid @RequestBody PublisherRequest request) {
        log.debug("POST /api/publishers");
        return new ResponseEntity<>(
                publisherService.createPublisher(request),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<List<PublisherResponse>> getAllPublishers() {
        log.debug("GET /api/publishers");
        return ResponseEntity.ok(publisherService.getAllPublisher());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PublisherResponse> getPublisher(@PathVariable Long id) {
        log.debug("GET /api/publishers/{}", id);
        return ResponseEntity.ok(publisherService.getPublisherById(id));
    }
}
