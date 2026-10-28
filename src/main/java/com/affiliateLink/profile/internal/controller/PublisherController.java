package com.affiliateLink.profile.internal.controller;

import com.affiliateLink.profile.internal.dto.PublisherRequest;
import com.affiliateLink.profile.internal.dto.PublisherResponse;
import com.affiliateLink.profile.internal.entity.Publisher;
import com.affiliateLink.profile.internal.service.PublisherService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/publishers")
public class PublisherController {

    private final PublisherService publisherService;

    public PublisherController(PublisherService publisherService) {
        this.publisherService = publisherService;
    }

    @PostMapping
    public ResponseEntity<PublisherResponse> addPublisher(
            @Valid @RequestBody PublisherRequest request) {

        return new ResponseEntity<>(
                publisherService.createPublisher(request),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<List<Publisher>> getAllPublishers() {
        return ResponseEntity.ok(publisherService.getAllPublisher());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Publisher> getPublisher(@PathVariable Long id) {
        return ResponseEntity.ok(publisherService.getPublisherById(id));
    }
}