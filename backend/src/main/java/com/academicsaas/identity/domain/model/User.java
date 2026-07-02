package com.academicsaas.identity.domain.model;

import com.academicsaas.identity.domain.model.valueobject.Email;
import com.academicsaas.identity.domain.model.valueobject.InstitutionId;
import com.academicsaas.identity.domain.model.valueobject.UserId;
import com.academicsaas.identity.domain.model.valueobject.UserStatus;
import com.academicsaas.shared.exception.ValidationException;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class User {

    private final UserId id;
    private Email email;
    private String passwordHash;
    private String firstName;
    private String lastName;
    private String phone;
    private UserStatus status;
    private InstitutionId institutionId;
    private final Set<Role> roles;
    private Instant lastLoginAt;
    private final Instant createdAt;
    private Instant updatedAt;

    public User(
        UserId id,
        Email email,
        String passwordHash,
        String firstName,
        String lastName,
        String phone,
        UserStatus status,
        InstitutionId institutionId,
        Set<Role> roles,
        Instant lastLoginAt,
        Instant createdAt,
        Instant updatedAt
    ) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.email = Objects.requireNonNull(email, "email must not be null");
        this.passwordHash = Objects.requireNonNull(passwordHash, "passwordHash must not be null");
        this.firstName = Objects.requireNonNull(firstName, "firstName must not be null");
        this.lastName = Objects.requireNonNull(lastName, "lastName must not be null");
        this.phone = phone;
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.institutionId = institutionId;
        this.roles = roles != null ? new HashSet<>(roles) : new HashSet<>();
        this.lastLoginAt = lastLoginAt;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt must not be null");
    }

    public static User create(UserId id, Email email, String passwordHash, String firstName, String lastName, InstitutionId institutionId) {
        var now = Instant.now();
        return new User(
            id,
            email,
            passwordHash,
            firstName,
            lastName,
            null,
            UserStatus.ACTIVE,
            institutionId,
            new HashSet<>(),
            null,
            now,
            now
        );
    }

    public void assignRole(Role role) {
        Objects.requireNonNull(role, "Role must not be null");
        if (status == UserStatus.INACTIVE) {
            throw new ValidationException("Cannot assign role to an inactive user");
        }
        if (roles.contains(role)) {
            return;
        }
        roles.add(role);
        updatedAt = Instant.now();
    }

    public void changePassword(String newPasswordHash) {
        this.passwordHash = Objects.requireNonNull(newPasswordHash, "newPasswordHash must not be null");
        this.updatedAt = Instant.now();
    }

    public void removeRole(Role role) {
        Objects.requireNonNull(role, "Role must not be null");
        if (!roles.contains(role)) {
            return;
        }
        roles.remove(role);
        updatedAt = Instant.now();
    }

    public void deactivate() {
        if (status == UserStatus.INACTIVE) {
            throw new ValidationException("User is already inactive");
        }
        this.status = UserStatus.INACTIVE;
        this.updatedAt = Instant.now();
    }

    public void lock() {
        if (status == UserStatus.LOCKED) {
            return;
        }
        this.status = UserStatus.LOCKED;
        this.updatedAt = Instant.now();
    }

    public void activate() {
        if (status == UserStatus.ACTIVE) {
            return;
        }
        this.status = UserStatus.ACTIVE;
        this.updatedAt = Instant.now();
    }

    public void recordLogin() {
        this.lastLoginAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public boolean hasRole(String roleName) {
        return roles.stream().anyMatch(r -> r.getName().equals(roleName));
    }

    public boolean hasPermission(String permissionCode) {
        return roles.stream().anyMatch(r -> r.hasPermission(permissionCode));
    }

    public UserId getId() { return id; }
    public Email getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getPhone() { return phone; }
    public UserStatus getStatus() { return status; }
    public InstitutionId getInstitutionId() { return institutionId; }
    public Set<Role> getRoles() { return Collections.unmodifiableSet(roles); }
    public Instant getLastLoginAt() { return lastLoginAt; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
