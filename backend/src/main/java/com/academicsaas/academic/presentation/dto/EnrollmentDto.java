package com.academicsaas.academic.presentation.dto;

import java.time.Instant;
import java.util.UUID;

public record EnrollmentDto(
    UUID id, UUID studentId, UUID sectionId,
    String status, Instant enrolledAt, Instant withdrawnAt,
    Instant createdAt, Instant updatedAt
) {}
