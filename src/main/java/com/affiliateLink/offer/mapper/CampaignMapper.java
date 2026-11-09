package com.affiliateLink.offer.mapper;

import com.affiliateLink.offer.CampaignInfo;
import com.affiliateLink.offer.dto.CampaignResponse;
import com.affiliateLink.offer.dto.CreateCampaignRequest;
import com.affiliateLink.offer.entity.Campaign;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CampaignMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)   // always DRAFT at creation
    @Mapping(target = "offer", ignore = true)
    Campaign toEntity(CreateCampaignRequest request);

    CampaignResponse toResponse(Campaign campaign);

    List<CampaignResponse> toResponses(List<Campaign> campaigns);

    @Mapping(target = "offerId", source = "offer.id")
    @Mapping(target = "advertiserId", source = "offer.advertiserId")
    CampaignInfo toInfo(Campaign campaign);
}
