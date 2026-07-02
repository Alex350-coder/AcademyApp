package com.academicsaas.academic.presentation.dto;

import java.util.UUID;

public record StudentListDto(
    UUID id, String enrollmentCode, String fullName, String email,
    String guardian, String status
) {}