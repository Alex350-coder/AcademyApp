package com.academicsaas.identity.domain.model.valueobject;

public enum UserStatus {
    ACTIVE,
    INACTIVE,
    LOCKED,
    PENDING;

    public boolean canAuthenticate() {
        return this == ACTIVE;
    }
}
