
package com.affiliateLink.offer.entity;

import com.affiliateLink.offer.entity.enumeration.CampaignStatus;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import com.affiliateLink.common.ConflictException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import com.affiliateLink.common.BadRequestException;
import com.affiliateLink.offer.entity.enumeration.PayoutModel;
import java.math.BigDecimal;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CampaignTest {

    @Test
    void shouldChangeStatusFromDraftToActive() {

        // Given
        Campaign campaign = new Campaign();

        LocalDate today = LocalDate.now();

        campaign.setEndDate(today.plusDays(10));

        // Verify default status
        assertThat(campaign.getStatus())
                .isEqualTo(CampaignStatus.DRAFT);

        // When
        campaign.changeStatus(CampaignStatus.ACTIVE, today);

        // Then
        assertThat(campaign.getStatus())
                .isEqualTo(CampaignStatus.ACTIVE);
    }

    @Test
    void shouldRejectInvalidStatusTransition() {

        // Given
        Campaign campaign = new Campaign();
        LocalDate today = LocalDate.now();

        campaign.setEndDate(today.plusDays(10));

        // When & Then
        assertThatThrownBy(() ->
                campaign.changeStatus(CampaignStatus.ENDED, today)
        ).isInstanceOf(ConflictException.class);

        // Status must remain unchanged
        assertThat(campaign.getStatus())
                .isEqualTo(CampaignStatus.DRAFT);
    }



    @Test
    void shouldRejectCpaPercentageAbove100() {

        // Given
        Campaign campaign = new Campaign();

        campaign.setStartDate(LocalDate.of(2026, 11, 10));
        campaign.setEndDate(LocalDate.of(2026, 11, 20));
        campaign.setBudget(new BigDecimal("1000"));
        campaign.setPayoutModel(PayoutModel.CPA_PERCENT);
        campaign.setPayoutValue(new BigDecimal("120"));

        // When & Then
        assertThatThrownBy(campaign::validateRules)
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Percentage must be greater than 0 and at most 100");
    }





    @ParameterizedTest
    @ValueSource(strings = {"0", "-10"})
    void shouldRejectZeroOrNegativePayout(String value) {

        // Given
        Campaign campaign = new Campaign();

        campaign.setStartDate(LocalDate.of(2026, 11, 10));
        campaign.setEndDate(LocalDate.of(2026, 11, 20));
        campaign.setBudget(new BigDecimal("1000"));
        campaign.setPayoutModel(PayoutModel.CPC);
        campaign.setPayoutValue(new BigDecimal(value));

        // When & Then
        assertThatThrownBy(campaign::validateRules)
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Payout amount must be greater than 0");
    }




    @Test
    void shouldRejectEndDateBeforeStartDate() {

        // Given
        Campaign campaign = new Campaign();

        campaign.setStartDate(LocalDate.of(2026, 11, 20));
        campaign.setEndDate(LocalDate.of(2026, 11, 10));

        campaign.setBudget(new BigDecimal("1000"));
        campaign.setPayoutModel(PayoutModel.CPC);
        campaign.setPayoutValue(new BigDecimal("10"));

        // When & Then
        assertThatThrownBy(campaign::validateRules)
                .isInstanceOf(BadRequestException.class)
                .hasMessage("End date must be after start date");
    }




    @Test
    void shouldRejectActivationWhenCampaignExpired() {

        // Given
        Campaign campaign = new Campaign();

        LocalDate today = LocalDate.of(2026, 11, 10);

        campaign.setEndDate(today.minusDays(1));

        // When & Then
        assertThatThrownBy(() ->
                campaign.changeStatus(CampaignStatus.ACTIVE, today)
        )
                .isInstanceOf(ConflictException.class)
                .hasMessage(
                        "The end date has passed: this campaign cannot be activated"
                );

        // Status must remain DRAFT
        assertThat(campaign.getStatus())
                .isEqualTo(CampaignStatus.DRAFT);
    }
}
