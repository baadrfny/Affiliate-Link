package com.affiliateLink.profile.service;

import com.affiliateLink.common.EmailAlreadyExistsException;
import com.affiliateLink.common.ResourceNotFoundException;
import com.affiliateLink.profile.dto.PublisherRequest;
import com.affiliateLink.profile.dto.PublisherResponse;
import com.affiliateLink.profile.entity.Publisher;
import com.affiliateLink.profile.mapper.PublisherMapper;
import com.affiliateLink.profile.repository.ProfileRepository;
import com.affiliateLink.profile.repository.PublisherRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PublisherService {

    private static final Logger log = LoggerFactory.getLogger(PublisherService.class);

    private final PublisherRepository publisherRepository;
    private final ProfileRepository profileRepository;
    private final PublisherMapper publisherMapper;

    public PublisherService(PublisherRepository publisherRepository,
                            ProfileRepository profileRepository,
                            PublisherMapper publisherMapper) {
        this.publisherRepository = publisherRepository;
        this.profileRepository = profileRepository;
        this.publisherMapper = publisherMapper;
    }

    @Transactional
    public PublisherResponse createPublisher(PublisherRequest request) {

        if (profileRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        Publisher saved = publisherRepository.save(publisherMapper.toEntity(request));
        log.info("Publisher created: id={}", saved.getId());

        return publisherMapper.toResponse(saved);
    }

    public PublisherResponse getPublisherById(Long id) {
        log.debug("Reading publisher {}", id);
        Publisher publisher = publisherRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Publisher not found with id: " + id));
        return publisherMapper.toResponse(publisher);
    }

    public List<PublisherResponse> getAllPublisher() {
        log.debug("Reading all publishers");
        return publisherMapper.toResponses(publisherRepository.findAll());
    }
}
