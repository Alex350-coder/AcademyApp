package com.academicsaas.academic.presentation.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record PeriodDto(
    UUID id, String name, LocalDate startDate, LocalDate endDate,
    String status, Instant createdAt, Instant updatedAt
) {}
