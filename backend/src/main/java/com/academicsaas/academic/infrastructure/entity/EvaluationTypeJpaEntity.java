package com.academicsaas.academic.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "evaluation_types")
public class EvaluationTypeJpaEntity {

    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "weight_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal weightPercentage;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public EvaluationTypeJpaEntity() {}

    public EvaluationTypeJpaEntity(UUID id, String name, BigDecimal weightPercentage, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.weightPercentage = weightPercentage;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getWeightPercentage() { return weightPercentage; }
    public void setWeightPercentage(BigDecimal weightPercentage) { this.weightPercentage = weightPercentage; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
