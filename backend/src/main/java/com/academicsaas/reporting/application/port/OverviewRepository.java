package com.academicsaas.reporting.application.port;

import com.academicsaas.reporting.domain.model.InstitutionalOverview;
import java.math.BigDecimal;

public interface OverviewRepository {
    InstitutionalOverview getInstitutionalOverview();
    long countStudents();
    long countTeachers();
    long countActiveSections();
    BigDecimal calculateOverallAverage();
    BigDecimal calculateOverallAttendanceRate();
}
