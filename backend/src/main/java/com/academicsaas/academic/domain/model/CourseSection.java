package com.academicsaas.academic.domain.model;

import com.academicsaas.academic.domain.exception.SectionCapacityExceededException;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class CourseSection {

    private final UUID id;
    private final UUID courseId;
    private final UUID academicPeriodId;
    private final UUID teacherId;
    private UUID classroomId;
    private String name;
    private int capacity;
    private int enrolledCount;
    private final Instant createdAt;
    private Instant updatedAt;

    public CourseSection(UUID id, UUID courseId, UUID academicPeriodId, UUID teacherId,
                         UUID classroomId, String name, int capacity, int enrolledCount,
                         Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNull(id);
        this.courseId = Objects.requireNonNull(courseId);
        this.academicPeriodId = Objects.requireNonNull(academicPeriodId);
        this.teacherId = Objects.requireNonNull(teacherId);
        this.classroomId = classroomId;
        this.name = Objects.requireNonNull(name);
        this.capacity = capacity;
        this.enrolledCount = enrolledCount;
        this.createdAt = Objects.requireNonNull(createdAt);
        this.updatedAt = Objects.requireNonNull(updatedAt);
    }

    public void incrementEnrolledCount() {
        if (enrolledCount >= capacity) {
            throw new SectionCapacityExceededException(id, capacity);
        }
        enrolledCount++;
        updatedAt = Instant.now();
    }

    public void decrementEnrolledCount() {
        if (enrolledCount > 0) {
            enrolledCount--;
            updatedAt = Instant.now();
        }
    }

    public UUID getId() { return id; }
    public UUID getCourseId() { return courseId; }
    public UUID getAcademicPeriodId() { return academicPeriodId; }
    public UUID getTeacherId() { return teacherId; }
    public UUID getClassroomId() { return classroomId; }
    public String getName() { return name; }
    public int getCapacity() { return capacity; }
    public int getEnrolledCount() { return enrolledCount; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CourseSection that)) {
            return false;
        }
        return id.equals(that.id);
    }

    @Override
    public int hashCode() { return id.hashCode(); }
}
