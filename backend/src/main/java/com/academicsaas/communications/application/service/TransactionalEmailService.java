package com.academicsaas.communications.application.service;

import com.academicsaas.communications.application.port.EmailSender;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TransactionalEmailService {

    private static final Logger log = LoggerFactory.getLogger(TransactionalEmailService.class);
    private final EmailSender emailSender;

    public TransactionalEmailService(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendWelcomeEmail(String to, String fullName, String temporaryPassword) {
        var email = new EmailSender.Email(
            to,
            "Bienvenido a Academia SaaS",
            "welcome-email",
            Map.of(
                "fullName", fullName,
                "temporaryPassword", temporaryPassword,
                "loginUrl", "https://app.academiasaas.com/login"
            )
        );
        emailSender.send(email);
        log.info("Welcome email sent to {}", to);
    }

    public void sendPasswordResetEmail(String to, String resetLink) {
        var email = new EmailSender.Email(
            to,
            "Recuperación de Contraseña - Academia SaaS",
            "password-reset-email",
            Map.of(
                "resetLink", resetLink,
                "expiryHours", 1
            )
        );
        emailSender.send(email);
        log.info("Password reset email sent to {}", to);
    }
}
