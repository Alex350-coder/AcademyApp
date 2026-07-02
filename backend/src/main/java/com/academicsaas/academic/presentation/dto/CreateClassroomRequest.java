package com.academicsaas.academic.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.UUID;

public record CreateClassroomRequest(
    @NotBlank String name,
    @NotBlank String code,
    @PositiveOrZero int capacity,
    String location,
    String resources,
    @NotNull UUID institutionId
) {}
