package com.academicsaas.reporting.application.usecase;

import com.academicsaas.reporting.application.port.CourseReportRepository;
import com.academicsaas.reporting.domain.model.CoursePerformanceData;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class GetCoursePerformanceUseCase {
    private final CourseReportRepository courseReportRepository;
    public GetCoursePerformanceUseCase(CourseReportRepository courseReportRepository) {
        this.courseReportRepository = courseReportRepository;
    }
    public List<CoursePerformanceData> execute(UUID academicPeriodId) {
        return courseReportRepository.getCoursePerformance(academicPeriodId);
    }
}
