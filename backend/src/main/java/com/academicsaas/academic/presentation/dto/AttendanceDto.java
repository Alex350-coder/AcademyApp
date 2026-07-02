package com.academicsaas.academic.presentation.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record AttendanceDto(
    UUID id, UUID enrollmentId, LocalDate date,
    String status, String justification,
    Instant createdAt, Instant updatedAt
) {}
