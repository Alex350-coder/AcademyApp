package com.academicsaas.academic.presentation.controller;

import com.academicsaas.academic.application.usecase.EnrollStudentUseCase;
import com.academicsaas.academic.domain.model.Enrollment;
import com.academicsaas.academic.domain.repository.StudentRepository;
import com.academicsaas.academic.infrastructure.adapter.EnrollmentRepositoryAdapter;
import com.academicsaas.academic.presentation.dto.BulkEnrollmentRequest;
import com.academicsaas.academic.presentation.dto.CreateEnrollmentRequest;
import com.academicsaas.academic.presentation.dto.EnrollmentDto;
import com.academicsaas.identity.infrastructure.persistence.repository.SpringDataUserRepository;
import com.academicsaas.shared.event.CacheInvalidationService;
import com.academicsaas.shared.exception.NotFoundException;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/enrollments")
public class EnrollmentController {

    private final EnrollStudentUseCase enrollStudentUseCase;
    private final EnrollmentRepositoryAdapter enrollmentRepository;
    private final StudentRepository studentRepository;
    private final SpringDataUserRepository userRepository;
    private final CacheInvalidationService cacheInvalidationService;

    public EnrollmentController(EnrollStudentUseCase enrollStudentUseCase,
                                EnrollmentRepositoryAdapter enrollmentRepository,
                                StudentRepository studentRepository,
                                SpringDataUserRepository userRepository,
                                CacheInvalidationService cacheInvalidationService) {
        this.enrollStudentUseCase = enrollStudentUseCase;
        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.cacheInvalidationService = cacheInvalidationService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('DIRECTOR', 'SECRETARY')")
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody CreateEnrollmentRequest request) {
        var result = enrollStudentUseCase.execute(
            new EnrollStudentUseCase.Request(request.studentId(), request.sectionId()));
        cacheInvalidationService.evictAll("institutional-overview");
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(Map.of("enrollmentId", result.enrollmentId()));
    }

    @PostMapping("/bulk")
    @PreAuthorize("hasRole('DIRECTOR')")
    public ResponseEntity<Map<String, Object>> createBulk(@Valid @RequestBody BulkEnrollmentRequest request) {
        var results = request.studentIds().stream()
            .map(sid -> {
                try {
                    var result = enrollStudentUseCase.execute(
                        new EnrollStudentUseCase.Request(sid, request.sectionId()));
                    return Map.of("studentId", sid.toString(), "enrollmentId", result.enrollmentId().toString(), "success", true);
                } catch (Exception e) {
                    return Map.of("studentId", sid.toString(), "error", e.getMessage(), "success", false);
                }
            })
            .toList();
        cacheInvalidationService.evictAll("institutional-overview");
        return ResponseEntity.ok(Map.of("results", results));
    }

    @GetMapping("/pending-tasks")
    @PreAuthorize("hasAnyRole('DIRECTOR', 'SECRETARY')")
    public ResponseEntity<Map<String, Object>> pendingTasks() {
        // No "pending approval" enrollment status or per-section attendance-due
        // schedule exists in the domain model yet, so these are placeholders
        // until that workflow is designed rather than a guessed cross-tenant query.
        return ResponseEntity.ok(Map.of(
            "pendingEnrollments", 0,
            "unregisteredAttendance", 0));
    }

    @GetMapping("/section/{sectionId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'DIRECTOR', 'SECRETARY')")
    public ResponseEntity<List<EnrollmentDto>> listBySection(@PathVariable UUID sectionId) {
        var enrollments = enrollmentRepository.findBySectionId(sectionId);
        return ResponseEntity.ok(toDtos(enrollments));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'DIRECTOR')")
    public ResponseEntity<List<EnrollmentDto>> listByStudent(@PathVariable UUID studentId) {
        var enrollments = enrollmentRepository.findByStudentId(studentId);
        return ResponseEntity.ok(toDtos(enrollments));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('DIRECTOR')")
    public ResponseEntity<Void> withdraw(@PathVariable UUID id) {
        var enrollment = enrollmentRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Enrollment", id));
        enrollment.withdraw();
        enrollmentRepository.save(enrollment);
        cacheInvalidationService.evictAll("institutional-overview");
        return ResponseEntity.noContent().build();
    }

    private List<EnrollmentDto> toDtos(List<Enrollment> enrollments) {
        Map<UUID, String> studentNameCache = new HashMap<>();
        return enrollments.stream()
            .map(e -> new EnrollmentDto(
                e.getId(), e.getStudentId(),
                studentNameCache.computeIfAbsent(e.getStudentId(), this::resolveStudentName),
                e.getSectionId(), e.getStatus().name(), e.getEnrolledAt(), e.getWithdrawnAt(),
                e.getCreatedAt(), e.getUpdatedAt()))
            .toList();
    }

    private String resolveStudentName(UUID studentId) {
        return studentRepository.findById(studentId)
            .flatMap(student -> userRepository.findById(student.getUserId()))
            .map(user -> (user.getFirstName() + " " + user.getLastName()).trim())
            .orElse("Unknown");
    }
}
