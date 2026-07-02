package com.academicsaas.identity.domain.model;

import com.academicsaas.identity.domain.model.valueobject.InstitutionId;
import java.time.Instant;
import java.util.Objects;

public class Institution {

    private final InstitutionId id;
    private String name;
    private String code;
    private String address;
    private String phone;
    private String email;
    private boolean active;
    private final Instant createdAt;
    private Instant updatedAt;

    public Institution(
        InstitutionId id,
        String name,
        String code,
        String address,
        String phone,
        String email,
        boolean active,
        Instant createdAt,
        Instant updatedAt
    ) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.code = Objects.requireNonNull(code, "code must not be null");
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.active = active;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt must not be null");
    }

    public static Institution create(InstitutionId id, String name, String code, String address, String phone, String email) {
        var now = Instant.now();
        return new Institution(id, name, code, address, phone, email, true, now, now);
    }

    public void update(String name, String address, String phone, String email) {
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = Instant.now();
    }

    public void activate() {
        this.active = true;
        this.updatedAt = Instant.now();
    }

    public InstitutionId getId() { return id; }
    public String getName() { return name; }
    public String getCode() { return code; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public boolean isActive() { return active; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
