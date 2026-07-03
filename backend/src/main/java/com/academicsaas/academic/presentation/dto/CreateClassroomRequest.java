package com.academicsaas.academic.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record CreateClassroomRequest(
    @NotBlank String name,
    @NotBlank String code,
    @PositiveOrZero int capacity,
    String location,
    String resources
) {}
