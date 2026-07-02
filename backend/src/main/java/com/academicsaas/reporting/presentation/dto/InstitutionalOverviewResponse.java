package com.academicsaas.reporting.presentation.dto;

import java.math.BigDecimal;

public record InstitutionalOverviewResponse(
    long totalStudents,
    long totalTeachers,
    long totalActiveSections,
    BigDecimal overallAverageScore,
    BigDecimal overallAttendanceRate
) {}
