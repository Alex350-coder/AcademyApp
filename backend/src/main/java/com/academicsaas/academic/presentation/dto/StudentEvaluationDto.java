package com.academicsaas.academic.presentation.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record StudentEvaluationDto(
    UUID id, String name, BigDecimal score, BigDecimal maxScore,
    LocalDate date, String type
) {}
