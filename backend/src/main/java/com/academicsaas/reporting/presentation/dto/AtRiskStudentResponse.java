package com.academicsaas.reporting.presentation.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record AtRiskStudentResponse(
    UUID studentId,
    String studentName,
    BigDecimal currentAverage,
    String reason,
    UUID sectionId,
    String sectionName
) {}
