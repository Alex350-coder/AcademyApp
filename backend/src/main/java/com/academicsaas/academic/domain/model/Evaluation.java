package com.academicsaas.academic.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Evaluation {

    private final UUID id;
    private final UUID sectionId;
    private final UUID evaluationTypeId;
    private String name;
    private java.time.LocalDate date;
    private BigDecimal maxScore;
    private final Instant createdAt;
    private Instant updatedAt;

    public Evaluation(UUID id, UUID sectionId, UUID evaluationTypeId, String name,
                      java.time.LocalDate date, BigDecimal maxScore,
                      Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNull(id);
        this.sectionId = Objects.requireNonNull(sectionId);
        this.evaluationTypeId = Objects.requireNonNull(evaluationTypeId);
        this.name = Objects.requireNonNull(name);
        this.date = date;
        this.maxScore = Objects.requireNonNull(maxScore);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.updatedAt = Objects.requireNonNull(updatedAt);
    }

    public UUID getId() { return id; }
    public UUID getSectionId() { return sectionId; }
    public UUID getEvaluationTypeId() { return evaluationTypeId; }
    public String getName() { return name; }
    public java.time.LocalDate getDate() { return date; }
    public BigDecimal getMaxScore() { return maxScore; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Evaluation that)) return false;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() { return id.hashCode(); }
}
