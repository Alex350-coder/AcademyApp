package com.academicsaas.academic.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "period_averages")
public class PeriodAverageJpaEntity {

    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "student_id", nullable = false, columnDefinition = "UUID")
    private UUID studentId;

    @Column(name = "section_id", nullable = false, columnDefinition = "UUID")
    private UUID sectionId;

    @Column(name = "average_score", nullable = false, precision = 10, scale = 2)
    private BigDecimal averageScore;

    @Column(name = "calculated_at", nullable = false)
    private Instant calculatedAt;

    public PeriodAverageJpaEntity() {}

    public PeriodAverageJpaEntity(UUID id, UUID studentId, UUID sectionId,
                                  BigDecimal averageScore, Instant calculatedAt) {
        this.id = id;
        this.studentId = studentId;
        this.sectionId = sectionId;
        this.averageScore = averageScore;
        this.calculatedAt = calculatedAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getStudentId() { return studentId; }
    public void setStudentId(UUID studentId) { this.studentId = studentId; }
    public UUID getSectionId() { return sectionId; }
    public void setSectionId(UUID sectionId) { this.sectionId = sectionId; }
    public BigDecimal getAverageScore() { return averageScore; }
    public void setAverageScore(BigDecimal averageScore) { this.averageScore = averageScore; }
    public Instant getCalculatedAt() { return calculatedAt; }
    public void setCalculatedAt(Instant calculatedAt) { this.calculatedAt = calculatedAt; }
}
