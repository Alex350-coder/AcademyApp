package com.academicsaas.academic.presentation.dto;

import java.time.Instant;
import java.util.UUID;

public record CourseDto(
    UUID id, String name, String code, String description,
    int credits, Instant createdAt, Instant updatedAt
) {}
