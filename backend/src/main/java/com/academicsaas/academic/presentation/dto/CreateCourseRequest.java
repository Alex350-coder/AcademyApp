package com.academicsaas.academic.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record CreateCourseRequest(
    @NotBlank String name,
    @NotBlank String code,
    String description,
    @PositiveOrZero int credits
) {}
