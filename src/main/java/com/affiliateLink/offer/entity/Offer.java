package com.affiliateLink.offer.entity;

import com.affiliateLink.offer.entity.enumeration.Niche;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "offer_offers")
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Simple id: NO JPA relation to the profile module
    @Column(name = "advertiser_id", nullable = false)
    private Long advertiserId;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "landing_url", nullable = false, length = 500)
    private String landingUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "niche", nullable = false, length = 30)
    private Niche niche;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Bidirectional: Campaign is the owner side (it has the offer_id column)
    @OneToMany(mappedBy = "offer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Campaign> campaigns = new ArrayList<>();

    // Offer is the owner side of the many-to-many (it defines the join table)
    @ManyToMany
    @JoinTable(
            name = "offer_offers_tags",
            joinColumns = @JoinColumn(name = "offer_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    public Offer() {
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    // Keeps both sides of the relation in sync
    public void addCampaign(Campaign campaign) {
        campaigns.add(campaign);
        campaign.setOffer(this);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAdvertiserId() { return advertiserId; }
    public void setAdvertiserId(Long advertiserId) { this.advertiserId = advertiserId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLandingUrl() { return landingUrl; }
    public void setLandingUrl(String landingUrl) { this.landingUrl = landingUrl; }

    public Niche getNiche() { return niche; }
    public void setNiche(Niche niche) { this.niche = niche; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<Campaign> getCampaigns() { return campaigns; }
    public void setCampaigns(List<Campaign> campaigns) { this.campaigns = campaigns; }

    public Set<Tag> getTags() { return tags; }
    public void setTags(Set<Tag> tags) { this.tags = tags; }
}
