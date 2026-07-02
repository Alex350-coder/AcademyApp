package com.academicsaas.identity.domain.exception;

import com.academicsaas.shared.exception.DomainException;

public class DuplicateEmailException extends DomainException {

    public DuplicateEmailException(String email) {
        super("Email '%s' is already registered".formatted(email), "DUPLICATE_EMAIL");
    }
}
