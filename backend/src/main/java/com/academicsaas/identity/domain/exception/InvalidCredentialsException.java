package com.academicsaas.identity.domain.exception;

import com.academicsaas.shared.exception.DomainException;

public class InvalidCredentialsException extends DomainException {

    public InvalidCredentialsException() {
        super("Invalid credentials", "INVALID_CREDENTIALS");
    }
}
