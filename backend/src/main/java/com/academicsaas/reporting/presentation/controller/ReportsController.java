package com.academicsaas.reporting.presentation.controller;

import com.academicsaas.academic.application.usecase.DetectAtRiskStudentsUseCase;
import com.academicsaas.academic.infrastructure.repository.SpringDataCourseSectionRepository;
import com.academicsaas.academic.infrastructure.repository.SpringDataStudentRepository;
import com.academicsaas.identity.infrastructure.persistence.repository.SpringDataUserRepository;
import com.academicsaas.reporting.application.usecase.GetAttendanceTrendUseCase;
import com.academicsaas.reporting.application.usecase.GetCoursePerformanceUseCase;
import com.academicsaas.reporting.application.usecase.GetInstitutionalOverviewUseCase;
import com.academicsaas.reporting.presentation.dto.AtRiskStudentResponse;
import com.academicsaas.reporting.presentation.dto.AttendanceTrendResponse;
import com.academicsaas.reporting.presentation.dto.CoursePerformanceResponse;
import com.academicsaas.reporting.presentation.dto.InstitutionalOverviewResponse;
import com.academicsaas.shared.security.CurrentUserContext;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reports")
@PreAuthorize("hasRole('DIRECTOR')")
public class ReportsController {

    private final GetInstitutionalOverviewUseCase getInstitutionalOverviewUseCase;
    private final GetCoursePerformanceUseCase getCoursePerformanceUseCase;
    private final GetAttendanceTrendUseCase getAttendanceTrendUseCase;
    private final DetectAtRiskStudentsUseCase detectAtRiskStudentsUseCase;
    private final SpringDataStudentRepository studentRepository;
    private final SpringDataUserRepository userRepository;
    private final SpringDataCourseSectionRepository sectionRepository;
    private final CurrentUserContext currentUserContext;

    public ReportsController(GetInstitutionalOverviewUseCase getInstitutionalOverviewUseCase,
                             GetCoursePerformanceUseCase getCoursePerformanceUseCase,
                             GetAttendanceTrendUseCase getAttendanceTrendUseCase,
                             DetectAtRiskStudentsUseCase detectAtRiskStudentsUseCase,
                             SpringDataStudentRepository studentRepository,
                             SpringDataUserRepository userRepository,
                             SpringDataCourseSectionRepository sectionRepository,
                             CurrentUserContext currentUserContext) {
        this.getInstitutionalOverviewUseCase = getInstitutionalOverviewUseCase;
        this.getCoursePerformanceUseCase = getCoursePerformanceUseCase;
        this.getAttendanceTrendUseCase = getAttendanceTrendUseCase;
        this.detectAtRiskStudentsUseCase = detectAtRiskStudentsUseCase;
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.sectionRepository = sectionRepository;
        this.currentUserContext = currentUserContext;
    }

    @GetMapping("/institutional-overview")
    public ResponseEntity<InstitutionalOverviewResponse> getInstitutionalOverview(Authentication auth) {
        var overview = getInstitutionalOverviewUseCase.execute(currentUserContext.institutionId(auth));
        return ResponseEntity.ok(new InstitutionalOverviewResponse(
            overview.totalStudents(), overview.totalTeachers(),
            overview.totalActiveSections(), overview.overallAverageScore(),
            overview.overallAttendanceRate()));
    }

    @GetMapping("/at-risk-students")
    public ResponseEntity<List<AtRiskStudentResponse>> getAtRiskStudents(
            @RequestParam(required = false) UUID academicPeriodId,
            @RequestParam(required = false) BigDecimal passingThreshold,
            @RequestParam(required = false) String riskType,
            Authentication auth) {
        var result = detectAtRiskStudentsUseCase.execute(
            new DetectAtRiskStudentsUseCase.Request(
                currentUserContext.institutionId(auth), academicPeriodId, passingThreshold, riskType));

        Map<UUID, String> nameCache = new HashMap<>();
        Map<UUID, String> sectionNameCache = new HashMap<>();

        return ResponseEntity.ok(result.stream()
            .map(s -> {
                var studentName = nameCache.computeIfAbsent(s.studentId(), id -> {
                    var studentOpt = studentRepository.findById(id);
                    if (studentOpt.isEmpty()) {
                        return "Unknown";
                    }
                    var userOpt = userRepository.findById(studentOpt.get().getUserId());
                    if (userOpt.isEmpty()) {
                        return "Unknown";
                    }
                    var user = userOpt.get();
                    return (user.getFirstName() + " " + user.getLastName()).trim();
                });
                var sectionName = sectionNameCache.computeIfAbsent(s.sectionId(), id ->
                    sectionRepository.findById(id).map(sec -> sec.getName()).orElse("Unknown"));
                return new AtRiskStudentResponse(
                    s.studentId(), studentName, s.currentAverage(), s.reason(),
                    s.sectionId(), sectionName);
            })
            .toList());
    }

    @GetMapping("/course-performance")
    public ResponseEntity<List<CoursePerformanceResponse>> getCoursePerformance(
            @RequestParam(required = false) UUID academicPeriodId,
            Authentication auth) {
        var data = getCoursePerformanceUseCase.execute(currentUserContext.institutionId(auth), academicPeriodId);
        return ResponseEntity.ok(data.stream()
            .map(d -> new CoursePerformanceResponse(
                d.courseId(), d.courseName(), d.courseCode(),
                d.averageScore(), d.enrolledStudents(), d.attendanceRate()))
            .toList());
    }

    @GetMapping("/attendance-trend")
    public ResponseEntity<List<AttendanceTrendResponse>> getAttendanceTrend(
            @RequestParam(required = false) UUID academicPeriodId,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to,
            Authentication auth) {
        var data = getAttendanceTrendUseCase.execute(currentUserContext.institutionId(auth), academicPeriodId, from, to);
        return ResponseEntity.ok(data.stream()
            .map(d -> new AttendanceTrendResponse(
                d.date(), d.attendanceRate(), d.totalRecords(), d.presentRecords()))
            .toList());
    }
}
