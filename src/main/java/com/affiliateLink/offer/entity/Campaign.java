package com.affiliateLink.offer.entity;

import com.affiliateLink.common.BadRequestException;
import com.affiliateLink.common.ConflictException;
import com.affiliateLink.offer.entity.enumeration.CampaignStatus;
import com.affiliateLink.offer.entity.enumeration.PayoutModel;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

import static com.affiliateLink.offer.entity.enumeration.CampaignStatus.*;

@Entity
@Table(name = "offer_campaigns")
public class Campaign {

    // Current status -> statuses we are allowed to move to
    private static final Map<CampaignStatus, Set<CampaignStatus>> ALLOWED_TRANSITIONS = Map.of(
            DRAFT, Set.of(ACTIVE),
            ACTIVE, Set.of(PAUSED, ENDED),
            PAUSED, Set.of(ACTIVE, ENDED),
            ENDED, Set.of()
    );

    private static final BigDecimal HUNDRED = new BigDecimal("100");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "budget", nullable = false, precision = 15, scale = 2)
    private BigDecimal budget;

    @Enumerated(EnumType.STRING)
    @Column(name = "payout_model", nullable = false, length = 20)
    private PayoutModel payoutModel;

    @Column(name = "payout_value", nullable = false, precision = 15, scale = 2)
    private BigDecimal payoutValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private CampaignStatus status = DRAFT;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "offer_id", nullable = false)
    private Offer offer;

    public Campaign() {
    }

    // ---------- business rules ----------

    /** Rules checked when a campaign is created -> 400 */
    public void validateRules() {
        if (!endDate.isAfter(startDate)) {
            throw new BadRequestException("End date must be after start date");
        }
        if (budget.signum() <= 0) {
            throw new BadRequestException("Budget must be greater than 0");
        }
        if (payoutModel == PayoutModel.CPA_PERCENT) {
            if (payoutValue.signum() <= 0 || payoutValue.compareTo(HUNDRED) > 0) {
                throw new BadRequestException("Percentage must be greater than 0 and at most 100");
            }
        } else if (payoutValue.signum() <= 0) {
            throw new BadRequestException("Payout amount must be greater than 0");
        }
    }

    /** Checks a status change WITHOUT doing it -> 409 if refused */
    public void checkTransition(CampaignStatus target, LocalDate today) {
        if (!ALLOWED_TRANSITIONS.get(status).contains(target)) {
            throw new ConflictException(
                    "Transition " + status + " -> " + target + " is not allowed");
        }
        if (target == ACTIVE && endDate.isBefore(today)) {
            throw new ConflictException("The end date has passed: this campaign cannot be activated");
        }
    }

    public void changeStatus(CampaignStatus target, LocalDate today) {
        checkTransition(target, today);
        this.status = target;
    }

    // ---------- getters / setters ----------

    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public BigDecimal getBudget() { return budget; }
    public void setBudget(BigDecimal budget) { this.budget = budget; }

    public PayoutModel getPayoutModel() { return payoutModel; }
    public void setPayoutModel(PayoutModel payoutModel) { this.payoutModel = payoutModel; }

    public BigDecimal getPayoutValue() { return payoutValue; }
    public void setPayoutValue(BigDecimal payoutValue) { this.payoutValue = payoutValue; }

    public CampaignStatus getStatus() { return status; }
    public void setStatus(CampaignStatus status) { this.status = status; }

    public Offer getOffer() { return offer; }
    public void setOffer(Offer offer) { this.offer = offer; }
}
