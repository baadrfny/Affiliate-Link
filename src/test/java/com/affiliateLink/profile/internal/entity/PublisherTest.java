
package com.affiliateLink.profile.internal.entity;

import com.affiliateLink.profile.internal.entity.enumStatus.status;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class PublisherTest {

    @Test
    void shouldHavePendingStatusByDefault(){
        //Given
        Publisher publisher = new Publisher();

        //When
        status actualStatus = publisher.getStatu();

        //Then
        assertThat(actualStatus).isEqualTo(status.PENDING);
    }


    @Test
    void shouldChangeStatusFromPendingToActive(){
        //Given
        Publisher publisher = new Publisher();

        //When
        publisher.setStatu(status.ACTIVE);

        //Then
        assertThat(publisher.getStatu()).isEqualTo(status.ACTIVE);
    }


    @Test
    void shouldInitializeCreatedAtOnPersist(){
        Publisher publisher = new Publisher();
        publisher.onCreate();
        assertThat(publisher.getCreatedAt()).isNotNull();
        assertThat(publisher.getStatu()).isEqualTo(status.PENDING);
    }
}
