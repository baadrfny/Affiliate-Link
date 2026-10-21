package com.affiliateLink.profile.controller;


import com.affiliateLink.profile.entity.Publisher;
import com.affiliateLink.profile.service.PublisherService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/publishers")
public class PublisherController {

    private final PublisherService publisherService;

    public PublisherController(PublisherService publisherService){
        this.publisherService = publisherService;
    }

    @PostMapping()
    public ResponseEntity<Publisher> addPublisher(@Valid @RequestBody Publisher publisher){
       return new ResponseEntity<>(publisherService.createPublisher(publisher), HttpStatus.CREATED);
    }


    @GetMapping()
    public ResponseEntity<List<Publisher>> getAllPublishers(){
        List<Publisher> publishers = publisherService.getAllPublisher();
        return ResponseEntity.ok(publishers);
    }

    @GetMapping("/{id}")
    public ResponseEntity getPulisher(@PathVariable Long id){
        Optional<Publisher> publisher = publisherService.getPublisherById(id);
       return ResponseEntity.ok(publisher);
    }

}
