package com.academicsaas.academic.presentation.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record StudentGradesDto(
    UUID studentId,
    UUID sectionId,
    String sectionName,
    BigDecimal average,
    List<GradeDto> grades
) {}
