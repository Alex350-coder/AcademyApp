package com.academicsaas.academic.presentation.dto;

import jakarta.validation.constraints.PositiveOrZero;

public record UpdateCourseRequest(
    String name,
    String code,
    String description,
    @PositiveOrZero Integer credits
) {}
