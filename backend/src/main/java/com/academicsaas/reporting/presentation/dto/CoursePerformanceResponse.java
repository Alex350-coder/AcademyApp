package com.academicsaas.reporting.presentation.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CoursePerformanceResponse(
    UUID courseId,
    String courseName,
    String courseCode,
    BigDecimal averageScore,
    int enrolledStudents,
    BigDecimal attendanceRate
) {}
