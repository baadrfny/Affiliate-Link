
package com.affiliateLink.profile.internal.service;

import com.affiliateLink.profile.api.ProfileSuspended;
import com.affiliateLink.profile.internal.entity.Profile;
import com.affiliateLink.profile.internal.entity.Publisher;
import com.affiliateLink.profile.internal.entity.enumStatus.status;
import com.affiliateLink.profile.internal.repository.ProfileRepository;
import com.affiliateLink.profile.mapper.ProfileMapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private ProfileMapper profileMapper;

    @InjectMocks
    private ProfileService profileService;

    @Test
    void shouldPublishProfileSuspendedEvent() {

        // Given
        Long id = 99L;

        Profile profile = new Publisher();
        profile.setStatu(status.ACTIVE);

        when(profileRepository.findById(id))
                .thenReturn(Optional.of(profile));

        when(profileRepository.save(any(Profile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        profileService.changeStatus(id, status.SUSPENDED);

        // Then
        assertThat(profile.getStatu())
                .isEqualTo(status.SUSPENDED);

        verify(profileRepository).save(profile);

        verify(eventPublisher).publishEvent(
                (Object) argThat(event ->
                        event instanceof ProfileSuspended
                )
        );
    }
}
