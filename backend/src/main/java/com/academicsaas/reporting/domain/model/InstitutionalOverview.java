package com.academicsaas.reporting.domain.model;

import java.math.BigDecimal;

public record InstitutionalOverview(
    long totalStudents,
    long totalTeachers,
    long totalActiveSections,
    BigDecimal overallAverageScore,
    BigDecimal overallAttendanceRate
) {}
