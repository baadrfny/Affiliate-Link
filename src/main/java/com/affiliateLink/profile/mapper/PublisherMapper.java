package com.affiliateLink.profile.mapper;

import com.affiliateLink.profile.api.PublisherContact;
import com.affiliateLink.profile.internal.dto.PublisherRequest;
import com.affiliateLink.profile.internal.dto.PublisherResponse;
import com.affiliateLink.profile.internal.entity.Publisher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PublisherMapper {

    @Mapping(target = "statu", ignore = true)
    Publisher toEntity(PublisherRequest request);

    PublisherResponse toResponse(Publisher publisher);

    List<PublisherResponse> toResponses(List<Publisher> publishers);

    // used by ProfileApi (public API of the module)
    PublisherContact toContact(Publisher publisher);

    List<PublisherContact> toContacts(List<Publisher> publishers);
}
