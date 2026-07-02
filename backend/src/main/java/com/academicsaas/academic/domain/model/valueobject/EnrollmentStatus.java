package com.academicsaas.academic.domain.model.valueobject;

public enum EnrollmentStatus {
    ACTIVE,
    WITHDRAWN,
    COMPLETED,
    CANCELLED;

    public boolean isActive() {
        return this == ACTIVE;
    }
}
