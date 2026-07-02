package com.academicsaas.academic.presentation.controller;

import com.academicsaas.academic.application.usecase.RegisterAttendanceUseCase;
import com.academicsaas.academic.infrastructure.adapter.AttendanceRepositoryAdapter;
import com.academicsaas.academic.infrastructure.adapter.EnrollmentRepositoryAdapter;
import com.academicsaas.academic.presentation.dto.AttendanceDto;
import com.academicsaas.academic.presentation.dto.BulkAttendanceRequest;
import com.academicsaas.shared.event.CacheInvalidationService;
import jakarta.validation.Valid;
import java.time.LocalDate;
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
    private final CacheInvalidationService cacheInvalidationService;

    public AttendanceController(RegisterAttendanceUseCase registerAttendanceUseCase,
                                AttendanceRepositoryAdapter attendanceRepository,
                                EnrollmentRepositoryAdapter enrollmentRepository,
                                CacheInvalidationService cacheInvalidationService) {
        this.registerAttendanceUseCase = registerAttendanceUseCase;
        this.attendanceRepository = attendanceRepository;
        this.enrollmentRepository = enrollmentRepository;
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
    @PreAuthorize("hasAnyRole('TEACHER', 'DIRECTOR')")
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
    @PreAuthorize("hasAnyRole('TEACHER', 'DIRECTOR')")
    public ResponseEntity<List<AttendanceDto>> getBySection(
            @PathVariable UUID sectionId,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to) {
        var enrollments = enrollmentRepository.findBySectionId(sectionId);
        var enrollmentIds = enrollments.stream().map(e -> e.getId()).toList();
        var records = attendanceRepository.findByEnrollmentIds(enrollmentIds);
        if (from != null) records = records.stream().filter(a -> !a.getDate().isBefore(from)).toList();
        if (to != null) records = records.stream().filter(a -> !a.getDate().isAfter(to)).toList();
        return ResponseEntity.ok(records.stream().map(this::toDto).toList());
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'DIRECTOR')")
    public ResponseEntity<List<AttendanceDto>> getByStudent(@PathVariable UUID studentId) {
        var enrollments = enrollmentRepository.findByStudentId(studentId);
        var enrollmentIds = enrollments.stream().map(e -> e.getId()).toList();
        var records = attendanceRepository.findByEnrollmentIds(enrollmentIds);
        return ResponseEntity.ok(records.stream().map(this::toDto).toList());
    }

    private AttendanceDto toDto(com.academicsaas.academic.domain.model.Attendance a) {
        return new AttendanceDto(a.getId(), a.getEnrollmentId(), a.getDate(),
            a.getStatus().name(), a.getJustification(),
            a.getCreatedAt(), a.getUpdatedAt());
    }
}
