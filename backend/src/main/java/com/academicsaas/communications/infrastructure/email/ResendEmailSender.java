package com.academicsaas.communications.infrastructure.email;

import com.academicsaas.communications.application.port.EmailSender;
import com.academicsaas.shared.config.ResendProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@ConditionalOnProperty(name = "app.email.provider", havingValue = "resend")
public class ResendEmailSender implements EmailSender {

    private static final Logger log = LoggerFactory.getLogger(ResendEmailSender.class);
    private final RestTemplate restTemplate;
    private final ResendProperties properties;

    public ResendEmailSender(ResendProperties properties) {
        this.properties = properties;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public void send(Email email) {
        try {
            var headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(properties.getApiKey());

            var request = new ResendRequest(
                properties.getFromEmail(),
                email.to(),
                email.subject(),
                renderTemplate(email.templateName(), email.templateVariables())
            );

            var entity = new HttpEntity<>(request, headers);

            restTemplate.postForEntity(
                "https://api.resend.com/emails",
                entity,
                String.class
            );

            log.info("Email sent via Resend to: {}", email.to());
        } catch (Exception e) {
            log.error("Failed to send email via Resend to: {}", email.to(), e);
        }
    }

    private String renderTemplate(String templateName, java.util.Map<String, Object> variables) {
        return switch (templateName) {
            case "welcome-email" -> """
                <h1>Bienvenido a Academia SaaS</h1>
                <p>Hola %s,</p>
                <p>Tu cuenta ha sido creada exitosamente.</p>
                <p>Tu contraseña temporal es: <strong>%s</strong></p>
                <p><a href="%s">Iniciar sesión</a></p>
                <p>Por seguridad, cambia tu contraseña al iniciar sesión.</p>
                """.formatted(
                    variables.getOrDefault("fullName", ""),
                    variables.getOrDefault("temporaryPassword", ""),
                    variables.getOrDefault("loginUrl", "")
                );
            case "password-reset-email" -> """
                <h1>Recuperación de Contraseña</h1>
                <p>Has solicitado restablecer tu contraseña.</p>
                <p><a href="%s">Restablecer contraseña</a></p>
                <p>Este enlace expira en %s hora(s).</p>
                <p>Si no solicitaste este cambio, ignora este mensaje.</p>
                """.formatted(
                    variables.getOrDefault("resetLink", ""),
                    variables.getOrDefault("expiryHours", 1)
                );
            default -> "<p>Mensaje de Academia SaaS</p>";
        };
    }

    private record ResendRequest(String from, String to, String subject, String html) {}
}
