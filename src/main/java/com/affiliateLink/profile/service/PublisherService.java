package com.affiliateLink.profile.service;

import com.affiliateLink.profile.entity.Publisher;
import com.affiliateLink.profile.repository.PublisherRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class PublisherService {

    private final PublisherRepository publisherRepository;



    public PublisherService(PublisherRepository publisherRepository){
        this.publisherRepository = publisherRepository;
    }

    public Publisher createPublisher(Publisher publisher){
        return publisherRepository.save(publisher);
    }


    public Optional<Publisher> getPublisherById(Long id){
        return publisherRepository.findById(id);
    }

    public List<Publisher> getAllPublisher(){
        return publisherRepository.findAll();
    }


}
