package com.academicsaas.academic.presentation.dto;

import java.time.LocalDate;
import java.util.UUID;

public record TeacherListDto(
    UUID id, String fullName, String email, String specialty,
    LocalDate hireDate, String status
) {}