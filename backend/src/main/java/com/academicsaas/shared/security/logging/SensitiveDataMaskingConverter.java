package com.academicsaas.shared.security.logging;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import java.util.regex.Pattern;

public class SensitiveDataMaskingConverter extends MessageConverter {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");

    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "(?i)(password|contraseña|passwd|secret|token|authorization)=\"?[^\\s&\"]+");

    private static final Pattern IDENTITY_DOC_PATTERN = Pattern.compile(
        "\\b\\d{3}[\\.\\-\\s]?\\d{3}[\\.\\-\\s]?\\d{3}[\\.\\-\\s]?\\d{2}\\b");

    private static final Pattern BEARER_TOKEN_PATTERN = Pattern.compile(
        "Bearer\\s+[A-Za-z0-9\\-._~+/]+=*");

    @Override
    public String convert(ILoggingEvent event) {
        var message = event.getFormattedMessage();
        if (message == null) {
            return null;
        }

        message = PASSWORD_PATTERN.matcher(message).replaceAll("$1=***MASKED***");
        message = EMAIL_PATTERN.matcher(message).replaceAll("***EMAIL***");
        message = IDENTITY_DOC_PATTERN.matcher(message).replaceAll("***DOC***");
        message = BEARER_TOKEN_PATTERN.matcher(message).replaceAll("Bearer ***MASKED***");

        return message;
    }
}
