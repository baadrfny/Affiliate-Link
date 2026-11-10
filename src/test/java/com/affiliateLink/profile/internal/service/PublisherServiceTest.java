
package com.affiliateLink.profile.internal.service;

import com.affiliateLink.common.EmailAlreadyExistsException;
import com.affiliateLink.common.ResourceNotFoundException;
import com.affiliateLink.profile.internal.dto.PublisherRequest;
import com.affiliateLink.profile.internal.repository.ProfileRepository;
import com.affiliateLink.profile.internal.repository.PublisherRepository;
import com.affiliateLink.profile.mapper.PublisherMapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PublisherServiceTest {

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private PublisherRepository publisherRepository;

    @Mock
    private PublisherMapper publisherMapper;

    @InjectMocks
    private PublisherService publisherService;

    @Test
    void shouldRejectDuplicateEmail() {

        // Given
        String email = "test@example.com";
        PublisherRequest request = mock(PublisherRequest.class);

        when(request.email()).thenReturn(email);
        when(profileRepository.existsByEmail(email))
                .thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> publisherService.createPublisher(request))
                .isInstanceOf(EmailAlreadyExistsException.class);

        verify(publisherRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenPublisherNotFound() {

        // Given
        Long id = 99L;

        when(publisherRepository.findById(id))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> publisherService.getPublisherById(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
