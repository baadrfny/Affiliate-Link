package com.affiliateLink.profile.internal.service;

import com.affiliateLink.common.EmailAlreadyExistsException;
import com.affiliateLink.common.ResourceNotFoundException;
import com.affiliateLink.profile.internal.dto.PublisherRequest;
import com.affiliateLink.profile.internal.dto.PublisherResponse;
import com.affiliateLink.profile.internal.entity.Publisher;
import com.affiliateLink.profile.internal.repository.ProfileRepository;
import com.affiliateLink.profile.internal.repository.PublisherRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PublisherService {

    private final PublisherRepository publisherRepository;
    private final ProfileRepository profileRepository;

    public PublisherService(
            PublisherRepository publisherRepository,
            ProfileRepository profileRepository) {
        this.publisherRepository = publisherRepository;
        this.profileRepository = profileRepository;
    }

    public PublisherResponse createPublisher(PublisherRequest request) {

        if (profileRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        Publisher publisher = new Publisher();

        publisher.setFirstName(request.firstName());
        publisher.setLastName(request.lastName());
        publisher.setEmail(request.email());
        publisher.setNiche(request.niche());
        publisher.setBio(request.bio());
        publisher.setCountry(request.country());

        Publisher savedPublisher = publisherRepository.save(publisher);

        return new PublisherResponse(
                savedPublisher.getId(),
                savedPublisher.getFirstName(),
                savedPublisher.getLastName(),
                savedPublisher.getEmail(),
                savedPublisher.getStatu(),
                savedPublisher.getNiche(),
                savedPublisher.getBio(),
                savedPublisher.getCountry(),
                savedPublisher.getCreatedAt()
        );
    }

    public Publisher getPublisherById(Long id) {
        return publisherRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Publisher not found with id: " + id
                        )
                );
    }

    public List<Publisher> getAllPublisher() {
        return publisherRepository.findAll();
    }
}