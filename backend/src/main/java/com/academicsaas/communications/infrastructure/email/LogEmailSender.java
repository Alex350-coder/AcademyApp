package com.academicsaas.communications.infrastructure.email;

import com.academicsaas.communications.application.port.EmailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnMissingBean(ResendEmailSender.class)
public class LogEmailSender implements EmailSender {

    private static final Logger log = LoggerFactory.getLogger(LogEmailSender.class);

    @Override
    public void send(Email email) {
        log.info("=== EMAIL (DEV MODE) ===");
        log.info("To: {}", email.to());
        log.info("Subject: {}", email.subject());
        log.info("Template: {}", email.templateName());
        log.info("Variables: {}", email.templateVariables());
        log.info("=========================");
    }
}
