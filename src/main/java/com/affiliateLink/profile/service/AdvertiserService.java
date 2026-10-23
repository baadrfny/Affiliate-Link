package com.affiliateLink.profile.service;

import com.affiliateLink.common.EmailAlreadyExistsException;
import com.affiliateLink.common.ResourceNotFoundException;
import com.affiliateLink.profile.dto.AdvertiserRequest;
import com.affiliateLink.profile.dto.AdvertiserResponse;
import com.affiliateLink.profile.entity.Advertiser;
import com.affiliateLink.profile.repository.AdvertiserRepository;
import com.affiliateLink.profile.repository.ProfileRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdvertiserService {

    private final AdvertiserRepository advertiserRepository;
    private final ProfileRepository profileRepository;

    public AdvertiserService(
            AdvertiserRepository advertiserRepository,
            ProfileRepository profileRepository) {
        this.advertiserRepository = advertiserRepository;
        this.profileRepository = profileRepository;
    }

    public AdvertiserResponse createAdvertiser(AdvertiserRequest request) {

        if (profileRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        Advertiser advertiser = new Advertiser();

        advertiser.setEmail(request.email());
        advertiser.setCompanyName(request.companyName());
        advertiser.setSector(request.sector());
        advertiser.setWebsite(request.website());

        Advertiser savedAdvertiser = advertiserRepository.save(advertiser);

        return new AdvertiserResponse(
                savedAdvertiser.getId(),
                savedAdvertiser.getCompanyName(),
                savedAdvertiser.getSector(),
                savedAdvertiser.getEmail(),
                savedAdvertiser.getStatu(),
                savedAdvertiser.getWebsite(),
                savedAdvertiser.getCreatedAt()
        );
    }

    public Advertiser getAdvertiserById(Long id) {
        return advertiserRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Advertiser not found with id: " + id
                        )
                );
    }

    public List<Advertiser> getAllAdvertisers() {
        return advertiserRepository.findAll();
    }
}