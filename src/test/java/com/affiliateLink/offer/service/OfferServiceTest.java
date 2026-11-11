package com.affiliateLink.offer.service;

import com.affiliateLink.offer.dto.CreateOfferRequest;
import com.affiliateLink.offer.mapper.OfferMapper;
import com.affiliateLink.offer.repository.OfferRepository;
import com.affiliateLink.offer.repository.TagRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OfferServiceTest {

    @Mock
    private OfferRepository offerRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private OfferMapper offerMapper;

    @Mock
    private AdvertiserChecker advertiserChecker;

    @InjectMocks
    private OfferService offerService;

    @Test
    void shouldRejectOfferWhenAdvertiserCannotPublish() {

        // Given
        CreateOfferRequest request = mock(CreateOfferRequest.class);

        when(request.advertiserId()).thenReturn(1L);

        doThrow(new IllegalStateException("Advertiser cannot publish"))
                .when(advertiserChecker)
                .checkCanPublish(1L);

        // When & Then
        assertThatThrownBy(() -> offerService.createOffer(request))
                .isInstanceOf(IllegalStateException.class);

        verify(offerRepository, never()).save(any());
    }
}