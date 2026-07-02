package com.academicsaas.shared.exception;

public class NotFoundException extends DomainException {

    public NotFoundException(String entityType, Object id) {
        super(
            "%s with id %s not found".formatted(entityType, id),
            "NOT_FOUND"
        );
    }

    public NotFoundException(String message) {
        super(message, "NOT_FOUND");
    }
}
