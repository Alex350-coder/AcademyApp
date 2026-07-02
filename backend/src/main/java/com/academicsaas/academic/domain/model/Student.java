package com.academicsaas.academic.domain.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class Student {
    private final UUID id;
    private final UUID userId;
    private String enrollmentCode;
    private LocalDate birthDate;
    private String guardianName;
    private String guardianContact;
    private final Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    public Student(UUID id, UUID userId, String enrollmentCode, LocalDate birthDate,
                   String guardianName, String guardianContact, Instant createdAt,
                   Instant updatedAt, Instant deletedAt) {
        this.id = Objects.requireNonNull(id);
        this.userId = Objects.requireNonNull(userId);
        this.enrollmentCode = Objects.requireNonNull(enrollmentCode);
        this.birthDate = birthDate;
        this.guardianName = guardianName;
        this.guardianContact = guardianContact;
        this.createdAt = Objects.requireNonNull(createdAt);
        this.updatedAt = Objects.requireNonNull(updatedAt);
        this.deletedAt = deletedAt;
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public String getEnrollmentCode() { return enrollmentCode; }
    public LocalDate getBirthDate() { return birthDate; }
    public String getGuardianName() { return guardianName; }
    public String getGuardianContact() { return guardianContact; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public Instant getDeletedAt() { return deletedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student student)) return false;
        return id.equals(student.id);
    }
    @Override
    public int hashCode() { return id.hashCode(); }
}
