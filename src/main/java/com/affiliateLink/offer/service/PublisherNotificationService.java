package com.affiliateLink.offer.service;

import com.affiliateLink.profile.api.ProfileApi;
import com.affiliateLink.profile.api.PublisherContact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
class PublisherNotificationService {

    private static final Logger log = LoggerFactory.getLogger(PublisherNotificationService.class);

    private final JavaMailSender mailSender;
    private final ProfileApi profileApi;

    PublisherNotificationService(JavaMailSender mailSender, ProfileApi profileApi) {
        this.mailSender = mailSender;
        this.profileApi = profileApi;
    }

    void notifyCampaignsPaused(Long advertiserId, List<String> offerTitles) {
        List<PublisherContact> publishers = profileApi.findActivePublishers();
        log.debug("Notifying {} publisher(s) about advertiser {}", publishers.size(), advertiserId);

        int sent = 0;
        int failed = 0;

        for (PublisherContact publisher : publishers) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom("no-reply@affiliatelink.com");
                message.setTo(publisher.email());
                message.setSubject("Please stop sharing some affiliate links");
                message.setText(buildText(publisher.firstName(), offerTitles));
                mailSender.send(message);
                sent++;
            } catch (MailException e) {
                failed++;
                // only the id and the error type: no email address in the logs
                log.error("Could not send email to publisher id={} ({})",
                        publisher.id(), e.getClass().getSimpleName());
            }
        }

        log.info("Notification finished for advertiser {}: {} sent, {} failed", advertiserId, sent, failed);
    }

    private String buildText(String firstName, List<String> offerTitles) {
        StringBuilder text = new StringBuilder();
        text.append("Hello ").append(firstName).append(",\n\n");
        text.append("The advertiser behind the following offers has been suspended, ")
                .append("and their campaigns are now paused:\n\n");
        for (String title : offerTitles) {
            text.append(" - ").append(title).append("\n");
        }
        text.append("\nPlease stop sharing the links of these offers on your social networks.\n\n");
        text.append("The AffiliateLink team");
        return text.toString();
    }
}
