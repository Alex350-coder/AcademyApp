package com.academicsaas.academic.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "grades")
public class GradeJpaEntity {

    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "evaluation_id", nullable = false, columnDefinition = "UUID")
    private UUID evaluationId;

    @Column(name = "student_id", nullable = false, columnDefinition = "UUID")
    private UUID studentId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal score;

    @Column(columnDefinition = "TEXT")
    private String comments;

    @Column(name = "graded_by", nullable = false, columnDefinition = "UUID")
    private UUID gradedBy;

    @Column(name = "graded_at", nullable = false)
    private Instant gradedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public GradeJpaEntity() {}

    public GradeJpaEntity(UUID id, UUID evaluationId, UUID studentId, BigDecimal score,
                          String comments, UUID gradedBy, Instant gradedAt,
                          Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.evaluationId = evaluationId;
        this.studentId = studentId;
        this.score = score;
        this.comments = comments;
        this.gradedBy = gradedBy;
        this.gradedAt = gradedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getEvaluationId() { return evaluationId; }
    public void setEvaluationId(UUID evaluationId) { this.evaluationId = evaluationId; }
    public UUID getStudentId() { return studentId; }
    public void setStudentId(UUID studentId) { this.studentId = studentId; }
    public BigDecimal getScore() { return score; }
    public void setScore(BigDecimal score) { this.score = score; }
    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
    public UUID getGradedBy() { return gradedBy; }
    public void setGradedBy(UUID gradedBy) { this.gradedBy = gradedBy; }
    public Instant getGradedAt() { return gradedAt; }
    public void setGradedAt(Instant gradedAt) { this.gradedAt = gradedAt; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
