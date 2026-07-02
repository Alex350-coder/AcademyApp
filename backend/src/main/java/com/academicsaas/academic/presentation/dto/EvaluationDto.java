package com.academicsaas.academic.presentation.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record EvaluationDto(
    UUID id, UUID sectionId, UUID evaluationTypeId, String evaluationTypeName,
    String name, LocalDate date, BigDecimal maxScore,
    Instant createdAt, Instant updatedAt
) {}
