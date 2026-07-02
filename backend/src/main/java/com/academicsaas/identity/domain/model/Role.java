package com.academicsaas.identity.domain.model;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class Role {

    private final UUID id;
    private final String name;
    private final String description;
    private final Set<Permission> permissions;

    public Role(UUID id, String name, String description, Set<Permission> permissions) {
        this.id = Objects.requireNonNull(id, "Role id must not be null");
        this.name = Objects.requireNonNull(name, "Role name must not be null");
        this.description = description;
        this.permissions = permissions != null
            ? Collections.unmodifiableSet(permissions)
            : Collections.emptySet();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public boolean hasPermission(String permissionCode) {
        return permissions.stream()
            .anyMatch(p -> p.getCode().equals(permissionCode));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Role role)) {
            return false;
        }
        return id.equals(role.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
