package com.academicsaas.reporting.infrastructure.adapter;

import com.academicsaas.academic.infrastructure.repository.SpringDataAttendanceRepository;
import com.academicsaas.academic.infrastructure.repository.SpringDataCourseRepository;
import com.academicsaas.academic.infrastructure.repository.SpringDataCourseSectionRepository;
import com.academicsaas.academic.infrastructure.repository.SpringDataEnrollmentRepository;
import com.academicsaas.reporting.application.port.AttendanceReportRepository;
import com.academicsaas.reporting.domain.model.AttendanceTrendData;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class AttendanceReportRepositoryAdapter implements AttendanceReportRepository {

    private final SpringDataAttendanceRepository attendanceRepository;
    private final SpringDataCourseSectionRepository sectionRepository;
    private final SpringDataEnrollmentRepository enrollmentRepository;
    private final SpringDataCourseRepository courseRepository;

    public AttendanceReportRepositoryAdapter(SpringDataAttendanceRepository attendanceRepository,
                                             SpringDataCourseSectionRepository sectionRepository,
                                             SpringDataEnrollmentRepository enrollmentRepository,
                                             SpringDataCourseRepository courseRepository) {
        this.attendanceRepository = attendanceRepository;
        this.sectionRepository = sectionRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public List<AttendanceTrendData> getAttendanceTrend(UUID institutionId, UUID academicPeriodId, LocalDate from, LocalDate to) {
        var ownCourseIds = courseRepository.findByInstitutionId(institutionId).stream()
            .map(c -> c.getId())
            .collect(Collectors.toSet());

        var sections = (academicPeriodId != null
            ? sectionRepository.findByAcademicPeriodId(academicPeriodId)
            : sectionRepository.findAll()).stream()
            .filter(s -> ownCourseIds.contains(s.getCourseId()))
            .toList();

        var enrollmentIds = sections.stream()
            .flatMap(s -> enrollmentRepository.findBySectionId(s.getId()).stream())
            .map(e -> e.getId())
            .toList();

        if (enrollmentIds.isEmpty()) {
            return List.of();
        }

        var allAttendance = attendanceRepository.findByEnrollmentIdIn(enrollmentIds);

        var filtered = allAttendance.stream()
            .filter(a -> (from == null || !a.getDate().isBefore(from))
                      && (to == null || !a.getDate().isAfter(to)))
            .collect(Collectors.groupingBy(a -> a.getDate()));

        return filtered.entrySet().stream()
            .map(entry -> {
                var date = entry.getKey();
                var records = entry.getValue();
                var total = records.size();
                var present = (int) records.stream()
                    .filter(a -> "PRESENT".equals(a.getStatus()) || "LATE".equals(a.getStatus()))
                    .count();
                var rate = total > 0
                    ? BigDecimal.valueOf(present)
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;
                return new AttendanceTrendData(date, rate, total, present);
            })
            .sorted((a, b) -> a.date().compareTo(b.date()))
            .toList();
    }
}
