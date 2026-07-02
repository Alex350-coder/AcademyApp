package com.academicsaas.academic.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record BulkAttendanceRequest(
    @NotNull UUID sectionId,
    @NotNull LocalDate date,
    @NotNull List<SingleAttendanceItem> attendances
) {
    public record SingleAttendanceItem(
        @NotNull UUID enrollmentId,
        @NotBlank String status
    ) {}
}
