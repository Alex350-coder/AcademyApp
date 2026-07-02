package com.academicsaas.academic.domain.model;

import com.academicsaas.academic.domain.model.valueobject.AttendanceStatus;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Attendance {

    private final UUID id;
    private final UUID enrollmentId;
    private final java.time.LocalDate date;
    private AttendanceStatus status;
    private String justification;
    private final Instant createdAt;
    private Instant updatedAt;

    public Attendance(UUID id, UUID enrollmentId, java.time.LocalDate date,
                      AttendanceStatus status, String justification,
                      Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNull(id);
        this.enrollmentId = Objects.requireNonNull(enrollmentId);
        this.date = Objects.requireNonNull(date);
        this.status = Objects.requireNonNull(status);
        this.justification = justification;
        this.createdAt = Objects.requireNonNull(createdAt);
        this.updatedAt = Objects.requireNonNull(updatedAt);
    }

    public static Attendance create(UUID id, UUID enrollmentId, java.time.LocalDate date, AttendanceStatus status) {
        var now = Instant.now();
        return new Attendance(id, enrollmentId, date, status, null, now, now);
    }

    public void justify(String justification) {
        this.justification = justification;
        this.status = AttendanceStatus.JUSTIFIED;
        this.updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getEnrollmentId() { return enrollmentId; }
    public java.time.LocalDate getDate() { return date; }
    public AttendanceStatus getStatus() { return status; }
    public String getJustification() { return justification; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Attendance that)) {
            return false;
        }
        return id.equals(that.id);
    }

    @Override
    public int hashCode() { return id.hashCode(); }
}
