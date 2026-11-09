package com.affiliateLink.profile.mapper;

import com.affiliateLink.profile.dto.ProfileStatusResponse;
import com.affiliateLink.profile.entity.Profile;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

    ProfileStatusResponse toStatusResponse(Profile profile);
}
