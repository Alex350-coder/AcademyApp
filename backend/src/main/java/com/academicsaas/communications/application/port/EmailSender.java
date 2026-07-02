package com.academicsaas.communications.application.port;

import java.util.Map;

public interface EmailSender {
    void send(Email email);

    record Email(
        String to,
        String subject,
        String templateName,
        Map<String, Object> templateVariables
    ) {}
}
