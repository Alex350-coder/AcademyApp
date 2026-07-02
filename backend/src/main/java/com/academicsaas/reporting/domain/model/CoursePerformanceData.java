package com.academicsaas.reporting.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

public record CoursePerformanceData(
    UUID courseId,
    String courseName,
    String courseCode,
    BigDecimal averageScore,
    int enrolledStudents,
    BigDecimal attendanceRate
) {}
