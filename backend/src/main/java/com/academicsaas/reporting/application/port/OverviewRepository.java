package com.academicsaas.reporting.application.port;

import com.academicsaas.reporting.domain.model.InstitutionalOverview;
import java.math.BigDecimal;
import java.util.UUID;

public interface OverviewRepository {
    InstitutionalOverview getInstitutionalOverview(UUID institutionId);
    long countStudents(UUID institutionId);
    long countTeachers(UUID institutionId);
    long countActiveSections(UUID institutionId);
    BigDecimal calculateOverallAverage(UUID institutionId);
    BigDecimal calculateOverallAttendanceRate(UUID institutionId);
}
