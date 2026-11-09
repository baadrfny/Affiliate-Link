package com.affiliateLink.profile.mapper;

import com.affiliateLink.profile.dto.AdvertiserRequest;
import com.affiliateLink.profile.dto.AdvertiserResponse;
import com.affiliateLink.profile.entity.Advertiser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AdvertiserMapper {

    // statu is not in the request: the entity keeps its default value (PENDING)
    @Mapping(target = "statu", ignore = true)
    Advertiser toEntity(AdvertiserRequest request);

    AdvertiserResponse toResponse(Advertiser advertiser);

    List<AdvertiserResponse> toResponses(List<Advertiser> advertisers);
}
