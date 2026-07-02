package com.academicsaas.identity.domain.model;

import java.util.Objects;
import java.util.UUID;

public class Permission {

    private final UUID id;
    private final String code;
    private final String description;

    public Permission(UUID id, String code, String description) {
        this.id = Objects.requireNonNull(id, "Permission id must not be null");
        this.code = Objects.requireNonNull(code, "Permission code must not be null");
        this.description = description;
    }

    public UUID getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Permission that)) {
            return false;
        }
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
