package com.academicsaas.academic.presentation.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record StudentAttendanceSummaryDto(
    UUID sectionId, String courseName, long presentCount, long totalCount, BigDecimal percentage
) {}
