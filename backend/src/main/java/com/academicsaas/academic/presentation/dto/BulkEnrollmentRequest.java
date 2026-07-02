package com.academicsaas.academic.presentation.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.UUID;

public record BulkEnrollmentRequest(
    @NotEmpty List<UUID> studentIds,
    @NotEmpty UUID sectionId
) {}
