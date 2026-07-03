package com.academicsaas.reporting.application.port;

import com.academicsaas.reporting.domain.model.CoursePerformanceData;
import java.util.List;
import java.util.UUID;

public interface CourseReportRepository {
    List<CoursePerformanceData> getCoursePerformance(UUID institutionId, UUID academicPeriodId);
}
