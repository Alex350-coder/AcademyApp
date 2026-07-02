package com.academicsaas.academic.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.util.UUID;

public record CreateSectionRequest(
    @NotBlank String name,
    UUID classroomId,
    @Positive int capacity,
    UUID courseId,
    UUID academicPeriodId,
    UUID teacherId
) {}
