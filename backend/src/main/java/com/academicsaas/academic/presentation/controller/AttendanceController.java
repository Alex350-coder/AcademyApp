package com.academicsaas.academic.presentation.controller;

import com.academicsaas.academic.application.usecase.RegisterAttendanceUseCase;
import com.academicsaas.academic.domain.model.Enrollment;
import com.academicsaas.academic.domain.repository.StudentRepository;
import com.academicsaas.academic.infrastructure.adapter.AttendanceRepositoryAdapter;
import com.academicsaas.academic.infrastructure.adapter.EnrollmentRepositoryAdapter;
import com.academicsaas.academic.presentation.dto.AttendanceDto;
import com.academicsaas.academic.presentation.dto.BulkAttendanceRequest;
import com.academicsaas.identity.infrastructure.persistence.repository.SpringDataUserRepository;
import com.academicsaas.shared.event.CacheInvalidationService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/attendance")
public class AttendanceController {

    private final RegisterAttendanceUseCase registerAttendanceUseCase;
    private final AttendanceRepositoryAdapter attendanceRepository;
    private final EnrollmentRepositoryAdapter enrollmentRepository;
    private final StudentRepository studentRepository;
    private final SpringDataUserRepository userRepository;
    private final CacheInvalidationService cacheInvalidationService;

    public AttendanceController(RegisterAttendanceUseCase registerAttendanceUseCase,
                                AttendanceRepositoryAdapter attendanceRepository,
                                EnrollmentRepositoryAdapter enrollmentRepository,
                                StudentRepository studentRepository,
                                SpringDataUserRepository userRepository,
                                CacheInvalidationService cacheInvalidationService) {
        this.registerAttendanceUseCase = registerAttendanceUseCase;
        this.attendanceRepository = attendanceRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.cacheInvalidationService = cacheInvalidationService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'DIRECTOR')")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterAttendanceUseCase.Request request) {
        var result = registerAttendanceUseCase.execute(request);
        cacheInvalidationService.evictAll("institutional-overview");
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(Map.of("attendanceId", result.attendanceId()));
    }

    @PostMapping("/bulk")
    @PreAuthorize("hasAnyRole('TEACHER', 'DIRECTOR', 'SECRETARY')")
    public ResponseEntity<Map<String, Object>> registerBulk(@Valid @RequestBody BulkAttendanceRequest request) {
        var bulkRequest = new RegisterAttendanceUseCase.BulkRequest(
            request.sectionId(),
            request.date(),
            request.attendances().stream()
                .map(a -> new RegisterAttendanceUseCase.SingleAttendance(a.enrollmentId(), a.status()))
                .toList()
        );
        var result = registerAttendanceUseCase.executeBulk(bulkRequest);
        cacheInvalidationService.evictAll("institutional-overview");
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(Map.of("registered", result.registered()));
    }

    @GetMapping("/section/{sectionId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'DIRECTOR', 'SECRETARY')")
    public ResponseEntity<List<AttendanceDto>> getBySection(
            @PathVariable UUID sectionId,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to) {
        var enrollments = enrollmentRepository.findBySectionId(sectionId);
        var enrollmentIds = enrollments.stream().map(e -> e.getId()).toList();
        var records = attendanceRepository.findByEnrollmentIds(enrollmentIds);
        if (from != null) {
            records = records.stream().filter(a -> !a.getDate().isBefore(from)).toList();
        }
        if (to != null) {
            records = records.stream().filter(a -> !a.getDate().isAfter(to)).toList();
        }
        return ResponseEntity.ok(toDtos(records, enrollments));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'DIRECTOR')")
    public ResponseEntity<List<AttendanceDto>> getByStudent(@PathVariable UUID studentId) {
        var enrollments = enrollmentRepository.findByStudentId(studentId);
        var enrollmentIds = enrollments.stream().map(e -> e.getId()).toList();
        var records = attendanceRepository.findByEnrollmentIds(enrollmentIds);
        return ResponseEntity.ok(toDtos(records, enrollments));
    }

    private List<AttendanceDto> toDtos(List<com.academicsaas.academic.domain.model.Attendance> records,
                                       List<Enrollment> enrollments) {
        Map<UUID, UUID> studentIdByEnrollmentId = new HashMap<>();
        for (var enrollment : enrollments) {
            studentIdByEnrollmentId.put(enrollment.getId(), enrollment.getStudentId());
        }
        Map<UUID, String> studentNameCache = new HashMap<>();
        return records.stream().map(a -> {
            var studentId = studentIdByEnrollmentId.get(a.getEnrollmentId());
            var studentName = studentId != null
                ? studentNameCache.computeIfAbsent(studentId, this::resolveStudentName)
                : null;
            return new AttendanceDto(a.getId(), a.getEnrollmentId(), studentId, studentName, a.getDate(),
                a.getStatus().name(), a.getJustification(),
                a.getCreatedAt(), a.getUpdatedAt());
        }).toList();
    }

    private String resolveStudentName(UUID studentId) {
        return studentRepository.findById(studentId)
            .flatMap(student -> userRepository.findById(student.getUserId()))
            .map(user -> (user.getFirstName() + " " + user.getLastName()).trim())
            .orElse("Unknown");
    }
}
