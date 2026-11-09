package com.affiliateLink.offer.mapper;

import com.affiliateLink.offer.dto.CreateOfferRequest;
import com.affiliateLink.offer.dto.OfferDetailResponse;
import com.affiliateLink.offer.dto.OfferResponse;
import com.affiliateLink.offer.dto.UpdateOfferRequest;
import com.affiliateLink.offer.entity.Offer;
import com.affiliateLink.offer.entity.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = CampaignMapper.class)
public interface OfferMapper {

    // Tags are handled in the service (they must be lowercased and not duplicated)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "campaigns", ignore = true)
    @Mapping(target = "tags", ignore = true)
    Offer toEntity(CreateOfferRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "advertiserId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "campaigns", ignore = true)
    @Mapping(target = "tags", ignore = true)
    void updateEntity(UpdateOfferRequest request, @MappingTarget Offer offer);

    OfferResponse toResponse(Offer offer);

    OfferDetailResponse toDetailResponse(Offer offer);

    // MapStruct uses this to convert Set<Tag> into Set<String>
    default String tagToName(Tag tag) {
        return tag.getName();
    }
}
