package com.academicsaas.academic.domain.exception;

import com.academicsaas.shared.exception.DomainException;

public class InvalidScoreException extends DomainException {

    public InvalidScoreException(String message) {
        super(message, "INVALID_SCORE");
    }
}
