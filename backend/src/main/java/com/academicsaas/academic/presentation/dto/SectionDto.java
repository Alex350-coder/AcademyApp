package com.academicsaas.academic.presentation.dto;

import java.time.Instant;
import java.util.UUID;

public record SectionDto(
    UUID id, UUID courseId, UUID academicPeriodId, UUID teacherId,
    UUID classroomId, String name, int capacity, int enrolledCount,
    String courseName, String teacherName,
    Instant createdAt, Instant updatedAt
) {}
