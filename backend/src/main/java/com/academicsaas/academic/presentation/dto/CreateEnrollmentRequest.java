package com.academicsaas.academic.presentation.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateEnrollmentRequest(
    @NotNull UUID studentId,
    @NotNull UUID sectionId
) {}
