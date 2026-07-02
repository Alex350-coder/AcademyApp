package com.academicsaas.identity.domain.model.valueobject;

import com.academicsaas.shared.exception.ValidationException;
import java.util.Objects;
import java.util.regex.Pattern;

public record Email(String value) {

    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

    public Email {
        Objects.requireNonNull(value, "Email must not be null");
        var normalized = value.trim().toLowerCase();
        if (normalized.isBlank()) {
            throw new ValidationException("Email must not be blank");
        }
        if (!EMAIL_PATTERN.matcher(normalized).matches()) {
            throw new ValidationException("Email has invalid format");
        }
        if (normalized.length() > 255) {
            throw new ValidationException("Email must not exceed 255 characters");
        }
    }

    @Override
    public String value() {
        return value;
    }

    public static Email of(String value) {
        return new Email(value);
    }
}
