package com.academicsaas.academic.presentation.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record GradeDto(
    UUID id, UUID evaluationId, UUID studentId,
    BigDecimal score, BigDecimal maxScore,
    String comments, UUID gradedBy, Instant gradedAt,
    Instant createdAt, Instant updatedAt
) {}
