package com.academicsaas.academic.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "students")
public class StudentJpaEntity {

    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true, columnDefinition = "UUID")
    private UUID userId;

    @Column(name = "enrollment_code", nullable = false, length = 20, unique = true)
    private String enrollmentCode;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "guardian_name", length = 200)
    private String guardianName;

    @Column(name = "guardian_contact", length = 100)
    private String guardianContact;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    public StudentJpaEntity() {}

    public StudentJpaEntity(UUID id, UUID userId, String enrollmentCode, LocalDate birthDate,
                            String guardianName, String guardianContact,
                            Instant createdAt, Instant updatedAt, Instant deletedAt) {
        this.id = id;
        this.userId = userId;
        this.enrollmentCode = enrollmentCode;
        this.birthDate = birthDate;
        this.guardianName = guardianName;
        this.guardianContact = guardianContact;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getEnrollmentCode() { return enrollmentCode; }
    public void setEnrollmentCode(String enrollmentCode) { this.enrollmentCode = enrollmentCode; }
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public String getGuardianName() { return guardianName; }
    public void setGuardianName(String guardianName) { this.guardianName = guardianName; }
    public String getGuardianContact() { return guardianContact; }
    public void setGuardianContact(String guardianContact) { this.guardianContact = guardianContact; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    public Instant getDeletedAt() { return deletedAt; }
    public void setDeletedAt(Instant deletedAt) { this.deletedAt = deletedAt; }
}
