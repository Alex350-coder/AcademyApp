package com.academicsaas.reporting.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AttendanceTrendData(
    LocalDate date,
    BigDecimal attendanceRate,
    int totalRecords,
    int presentRecords
) {}
