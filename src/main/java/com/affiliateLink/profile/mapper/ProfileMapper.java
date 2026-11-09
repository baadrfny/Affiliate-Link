package com.affiliateLink.profile.mapper;

import com.affiliateLink.profile.internal.dto.ProfileStatusResponse;
import com.affiliateLink.profile.internal.entity.Profile;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

    ProfileStatusResponse toStatusResponse(Profile profile);
}
