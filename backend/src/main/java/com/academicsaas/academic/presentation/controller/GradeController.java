package com.academicsaas.academic.presentation.controller;

import com.academicsaas.academic.application.usecase.CalculateStudentAverageUseCase;
import com.academicsaas.academic.application.usecase.RecordGradeUseCase;
import com.academicsaas.academic.domain.model.Grade;
import com.academicsaas.academic.infrastructure.adapter.GradeRepositoryAdapter;
import com.academicsaas.academic.infrastructure.adapter.EnrollmentRepositoryAdapter;
import com.academicsaas.academic.presentation.dto.GradeDto;
import com.academicsaas.academic.presentation.dto.RecordGradeRequest;
import com.academicsaas.academic.presentation.dto.StudentGradesDto;
import com.academicsaas.shared.event.CacheInvalidationService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/grades")
public class GradeController {

    private final RecordGradeUseCase recordGradeUseCase;
    private final CalculateStudentAverageUseCase calculateStudentAverageUseCase;
    private final GradeRepositoryAdapter gradeRepository;
    private final EnrollmentRepositoryAdapter enrollmentRepository;
    private final CacheInvalidationService cacheInvalidationService;

    public GradeController(RecordGradeUseCase recordGradeUseCase,
                           CalculateStudentAverageUseCase calculateStudentAverageUseCase,
                           GradeRepositoryAdapter gradeRepository,
                           EnrollmentRepositoryAdapter enrollmentRepository,
                           CacheInvalidationService cacheInvalidationService) {
        this.recordGradeUseCase = recordGradeUseCase;
        this.calculateStudentAverageUseCase = calculateStudentAverageUseCase;
        this.gradeRepository = gradeRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.cacheInvalidationService = cacheInvalidationService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'DIRECTOR')")
    public ResponseEntity<Map<String, Object>> record(@Valid @RequestBody RecordGradeRequest request,
                                                      Authentication auth) {
        var userId = UUID.fromString(auth.getName());
        var result = recordGradeUseCase.execute(
            new RecordGradeUseCase.Request(
                request.evaluationId(), request.studentId(),
                request.scoreValue(), userId));
        cacheInvalidationService.evictAll("institutional-overview");
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(Map.of("gradeId", result.gradeId()));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'DIRECTOR')")
    public ResponseEntity<List<StudentGradesDto>> getByStudent(
            @PathVariable UUID studentId,
            @RequestParam(required = false) UUID sectionId) {
        if (sectionId != null) {
            var avg = calculateStudentAverageUseCase.execute(
                new CalculateStudentAverageUseCase.Request(studentId, sectionId));
            var grades = gradeRepository.findByStudentIdAndSectionId(studentId, sectionId);
            var dto = new StudentGradesDto(
                studentId, sectionId, "Section-" + sectionId.toString().substring(0, 8),
                avg.average(),
                grades.stream().map(this::toGradeDto).toList());
            return ResponseEntity.ok(List.of(dto));
        }

        var enrollments = enrollmentRepository.findByStudentId(studentId);
        var result = enrollments.stream()
            .map(e -> {
                var secId = e.getSectionId();
                var avg = calculateStudentAverageUseCase.execute(
                    new CalculateStudentAverageUseCase.Request(studentId, secId));
                var grades = gradeRepository.findByStudentIdAndSectionId(studentId, secId);
                return new StudentGradesDto(
                    studentId, secId, "Section-" + secId.toString().substring(0, 8),
                    avg.average(),
                    grades.stream().map(this::toGradeDto).toList());
            })
            .toList();
        return ResponseEntity.ok(result);
    }

    private GradeDto toGradeDto(Grade g) {
        return new GradeDto(g.getId(), g.getEvaluationId(), g.getStudentId(),
            g.getScore().value(), g.getScore().maxScore(),
            g.getComments(), g.getGradedBy(), g.getGradedAt(),
            g.getCreatedAt(), g.getUpdatedAt());
    }
}
