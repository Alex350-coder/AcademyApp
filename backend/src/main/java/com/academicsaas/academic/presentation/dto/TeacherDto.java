package com.academicsaas.academic.presentation.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record TeacherDto(
    UUID id, UUID userId, String specialty, LocalDate hireDate,
    Instant createdAt, Instant updatedAt
) {}
