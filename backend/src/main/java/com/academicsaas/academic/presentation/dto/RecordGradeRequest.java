package com.academicsaas.academic.presentation.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.UUID;

public record RecordGradeRequest(
    @NotNull UUID evaluationId,
    @NotNull UUID studentId,
    @NotNull @PositiveOrZero BigDecimal scoreValue
) {}
