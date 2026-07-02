package com.academicsaas.academic.presentation.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record StudentDto(
    UUID id, UUID userId, String enrollmentCode,
    LocalDate birthDate, String guardianName, String guardianContact,
    Instant createdAt, Instant updatedAt
) {}
