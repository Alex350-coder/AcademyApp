package com.academicsaas.academic.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Course {

    private final UUID id;
    private String name;
    private String code;
    private String description;
    private int credits;
    private final UUID institutionId;
    private final Instant createdAt;
    private Instant updatedAt;

    public Course(UUID id, String name, String code, String description, int credits,
                  UUID institutionId, Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.code = Objects.requireNonNull(code);
        this.description = description;
        this.credits = credits;
        this.institutionId = Objects.requireNonNull(institutionId);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.updatedAt = Objects.requireNonNull(updatedAt);
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getCode() { return code; }
    public String getDescription() { return description; }
    public int getCredits() { return credits; }
    public UUID getInstitutionId() { return institutionId; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Course course)) return false;
        return id.equals(course.id);
    }

    @Override
    public int hashCode() { return id.hashCode(); }
}
