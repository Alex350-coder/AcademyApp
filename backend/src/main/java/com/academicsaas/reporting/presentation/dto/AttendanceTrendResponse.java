package com.academicsaas.reporting.presentation.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AttendanceTrendResponse(
    LocalDate date,
    BigDecimal attendanceRate,
    int totalRecords,
    int presentRecords
) {}
