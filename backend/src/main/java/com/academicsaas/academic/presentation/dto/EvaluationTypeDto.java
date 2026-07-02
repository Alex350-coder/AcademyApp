package com.academicsaas.academic.presentation.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record EvaluationTypeDto(UUID id, String name, BigDecimal weightPercentage) {}
