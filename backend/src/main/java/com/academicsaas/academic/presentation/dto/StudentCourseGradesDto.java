package com.academicsaas.academic.presentation.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record StudentCourseGradesDto(
    UUID sectionId, String courseName, String courseCode, String teacherName,
    BigDecimal average, List<StudentEvaluationDto> evaluations
) {}
