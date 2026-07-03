package com.academicsaas.academic.presentation.controller;

import com.academicsaas.academic.application.usecase.CalculateStudentAverageUseCase;
import com.academicsaas.academic.application.usecase.RecordGradeUseCase;
import com.academicsaas.academic.domain.model.Grade;
import com.academicsaas.academic.domain.repository.CourseRepository;
import com.academicsaas.academic.domain.repository.EvaluationRepository;
import com.academicsaas.academic.domain.repository.StudentRepository;
import com.academicsaas.academic.infrastructure.adapter.CourseSectionRepositoryAdapter;
import com.academicsaas.academic.infrastructure.adapter.GradeRepositoryAdapter;
import com.academicsaas.academic.infrastructure.adapter.EnrollmentRepositoryAdapter;
import com.academicsaas.academic.presentation.dto.GradeDto;
import com.academicsaas.academic.presentation.dto.RecordGradeRequest;
import com.academicsaas.academic.presentation.dto.StudentGradesDto;
import com.academicsaas.identity.infrastructure.persistence.repository.SpringDataUserRepository;
import com.academicsaas.shared.event.CacheInvalidationService;
import com.academicsaas.shared.exception.NotFoundException;
import com.academicsaas.shared.security.CurrentUserContext;
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
    private final EvaluationRepository evaluationRepository;
    private final CourseSectionRepositoryAdapter sectionRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final SpringDataUserRepository userRepository;
    private final CurrentUserContext currentUserContext;

    public GradeController(RecordGradeUseCase recordGradeUseCase,
                           CalculateStudentAverageUseCase calculateStudentAverageUseCase,
                           GradeRepositoryAdapter gradeRepository,
                           EnrollmentRepositoryAdapter enrollmentRepository,
                           CacheInvalidationService cacheInvalidationService,
                           EvaluationRepository evaluationRepository,
                           CourseSectionRepositoryAdapter sectionRepository,
                           CourseRepository courseRepository,
                           StudentRepository studentRepository,
                           SpringDataUserRepository userRepository,
                           CurrentUserContext currentUserContext) {
        this.recordGradeUseCase = recordGradeUseCase;
        this.calculateStudentAverageUseCase = calculateStudentAverageUseCase;
        this.gradeRepository = gradeRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.cacheInvalidationService = cacheInvalidationService;
        this.evaluationRepository = evaluationRepository;
        this.sectionRepository = sectionRepository;
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.currentUserContext = currentUserContext;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'DIRECTOR')")
    public ResponseEntity<Map<String, Object>> record(@Valid @RequestBody RecordGradeRequest request,
                                                      Authentication auth) {
        var evaluation = evaluationRepository.findById(request.evaluationId())
            .orElseThrow(() -> new NotFoundException("Evaluation", request.evaluationId()));
        requireSectionInOwnInstitution(evaluation.getSectionId(), auth);
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
            @RequestParam(required = false) UUID sectionId,
            Authentication auth) {
        var student = studentRepository.findById(studentId)
            .orElseThrow(() -> new NotFoundException("Student", studentId));
        var studentUser = userRepository.findById(student.getUserId()).orElse(null);
        if (studentUser == null || !currentUserContext.institutionId(auth).equals(studentUser.getInstitutionId())) {
            throw new NotFoundException("Student", studentId);
        }
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

    // CourseSection carries no institutionId of its own - ownership is
    // resolved transitively through its Course.
    private void requireSectionInOwnInstitution(UUID sectionId, Authentication auth) {
        var section = sectionRepository.findById(sectionId)
            .orElseThrow(() -> new NotFoundException("CourseSection", sectionId));
        var course = courseRepository.findById(section.getCourseId()).orElse(null);
        if (course == null || !course.getInstitutionId().equals(currentUserContext.institutionId(auth))) {
            throw new NotFoundException("CourseSection", sectionId);
        }
    }
}
