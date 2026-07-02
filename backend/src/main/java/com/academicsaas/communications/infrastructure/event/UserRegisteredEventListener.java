package com.academicsaas.communications.infrastructure.event;

import com.academicsaas.communications.application.service.TransactionalEmailService;
import com.academicsaas.identity.domain.event.UserRegisteredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class UserRegisteredEventListener {

    private static final Logger log = LoggerFactory.getLogger(UserRegisteredEventListener.class);
    private final TransactionalEmailService emailService;

    public UserRegisteredEventListener(TransactionalEmailService emailService) {
        this.emailService = emailService;
    }

    @EventListener
    public void onUserRegistered(UserRegisteredEvent event) {
        try {
            emailService.sendWelcomeEmail(event.email(), event.fullName(), "********");
            log.info("Welcome email triggered for user: {}", event.email());
        } catch (Exception e) {
            log.error("Failed to send welcome email to: {}", event.email(), e);
        }
    }
}
