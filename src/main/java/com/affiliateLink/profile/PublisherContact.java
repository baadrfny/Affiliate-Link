package com.affiliateLink.profile;

/**
 * Small read-only object given to other modules.
 * We never share our entities, only simple records like this one.
 */
public record PublisherContact(Long id, String firstName, String email) {
}
