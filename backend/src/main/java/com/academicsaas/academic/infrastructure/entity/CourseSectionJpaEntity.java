package com.academicsaas.academic.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "course_sections")
public class CourseSectionJpaEntity {

    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "course_id", nullable = false, columnDefinition = "UUID")
    private UUID courseId;

    @Column(name = "academic_period_id", nullable = false, columnDefinition = "UUID")
    private UUID academicPeriodId;

    @Column(name = "teacher_id", nullable = false, columnDefinition = "UUID")
    private UUID teacherId;

    @Column(name = "classroom_id", columnDefinition = "UUID")
    private UUID classroomId;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false)
    private int capacity;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public CourseSectionJpaEntity() {}

    public CourseSectionJpaEntity(UUID id, UUID courseId, UUID academicPeriodId, UUID teacherId,
                                  UUID classroomId, String name, int capacity,
                                  Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.courseId = courseId;
        this.academicPeriodId = academicPeriodId;
        this.teacherId = teacherId;
        this.classroomId = classroomId;
        this.name = name;
        this.capacity = capacity;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getCourseId() { return courseId; }
    public void setCourseId(UUID courseId) { this.courseId = courseId; }
    public UUID getAcademicPeriodId() { return academicPeriodId; }
    public void setAcademicPeriodId(UUID academicPeriodId) { this.academicPeriodId = academicPeriodId; }
    public UUID getTeacherId() { return teacherId; }
    public void setTeacherId(UUID teacherId) { this.teacherId = teacherId; }
    public UUID getClassroomId() { return classroomId; }
    public void setClassroomId(UUID classroomId) { this.classroomId = classroomId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
