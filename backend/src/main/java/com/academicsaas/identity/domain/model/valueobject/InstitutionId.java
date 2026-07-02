package com.academicsaas.identity.domain.model.valueobject;

import java.util.Objects;
import java.util.UUID;

public record InstitutionId(UUID value) {

    public InstitutionId {
        Objects.requireNonNull(value, "InstitutionId must not be null");
    }

    public static InstitutionId generate() {
        return new InstitutionId(UUID.randomUUID());
    }

    public static InstitutionId fromString(String value) {
        return new InstitutionId(UUID.fromString(value));
    }
}
