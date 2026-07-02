package com.academicsaas.academic.presentation.dto;

import java.time.Instant;
import java.util.UUID;

public record ClassroomDto(
    UUID id, String name, String code, int capacity,
    String location, String resources,
    Instant createdAt, Instant updatedAt
) {}
