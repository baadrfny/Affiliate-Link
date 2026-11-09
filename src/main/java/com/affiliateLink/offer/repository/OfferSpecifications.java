package com.affiliateLink.offer.repository;

import com.affiliateLink.offer.entity.Offer;
import com.affiliateLink.offer.entity.Tag;
import com.affiliateLink.offer.entity.enumeration.Niche;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public final class OfferSpecifications {

    private OfferSpecifications() {
    }

    public static Specification<Offer> hasNiche(Niche niche) {
        return (root, query, builder) -> builder.equal(root.get("niche"), niche);
    }

    public static Specification<Offer> hasAdvertiser(Long advertiserId) {
        return (root, query, builder) -> builder.equal(root.get("advertiserId"), advertiserId);
    }

    public static Specification<Offer> hasTag(String tagName) {
        return (root, query, builder) -> {
            Join<Offer, Tag> tags = root.join("tags");
            return builder.equal(tags.get("name"), tagName.trim().toLowerCase());
        };
    }

    public static Specification<Offer> titleContains(String keyword) {
        return (root, query, builder) -> builder.like(
                builder.lower(root.get("title")),
                "%" + keyword.trim().toLowerCase() + "%"
        );
    }
}
