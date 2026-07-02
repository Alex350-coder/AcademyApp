package com.academicsaas.academic.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "evaluations")
public class EvaluationJpaEntity {

    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "section_id", nullable = false, columnDefinition = "UUID")
    private UUID sectionId;

    @Column(name = "evaluation_type_id", nullable = false, columnDefinition = "UUID")
    private UUID evaluationTypeId;

    @Column(nullable = false, length = 200)
    private String name;

    private LocalDate date;

    @Column(name = "max_score", nullable = false, precision = 10, scale = 2)
    private BigDecimal maxScore;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public EvaluationJpaEntity() {}

    public EvaluationJpaEntity(UUID id, UUID sectionId, UUID evaluationTypeId, String name,
                               LocalDate date, BigDecimal maxScore,
                               Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.sectionId = sectionId;
        this.evaluationTypeId = evaluationTypeId;
        this.name = name;
        this.date = date;
        this.maxScore = maxScore;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getSectionId() { return sectionId; }
    public void setSectionId(UUID sectionId) { this.sectionId = sectionId; }
    public UUID getEvaluationTypeId() { return evaluationTypeId; }
    public void setEvaluationTypeId(UUID evaluationTypeId) { this.evaluationTypeId = evaluationTypeId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public BigDecimal getMaxScore() { return maxScore; }
    public void setMaxScore(BigDecimal maxScore) { this.maxScore = maxScore; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
