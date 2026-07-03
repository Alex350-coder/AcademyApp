package com.academicsaas.academic.presentation.controller;

import com.academicsaas.academic.domain.model.Evaluation;
import com.academicsaas.academic.domain.repository.CourseRepository;
import com.academicsaas.academic.infrastructure.adapter.CourseSectionRepositoryAdapter;
import com.academicsaas.academic.infrastructure.adapter.EvaluationRepositoryAdapter;
import com.academicsaas.academic.infrastructure.entity.EvaluationTypeJpaEntity;
import com.academicsaas.academic.infrastructure.repository.SpringDataEvaluationTypeRepository;
import com.academicsaas.academic.presentation.dto.CreateEvaluationRequest;
import com.academicsaas.academic.presentation.dto.EvaluationDto;
import com.academicsaas.academic.presentation.dto.EvaluationTypeDto;
import com.academicsaas.shared.exception.NotFoundException;
import com.academicsaas.shared.security.CurrentUserContext;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.HashMap;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/evaluations")
public class EvaluationController {

    private final EvaluationRepositoryAdapter evaluationRepository;
    private final SpringDataEvaluationTypeRepository evaluationTypeRepository;
    private final CourseSectionRepositoryAdapter sectionRepository;
    private final CourseRepository courseRepository;
    private final CurrentUserContext currentUserContext;

    public EvaluationController(EvaluationRepositoryAdapter evaluationRepository,
                                SpringDataEvaluationTypeRepository evaluationTypeRepository,
                                CourseSectionRepositoryAdapter sectionRepository,
                                CourseRepository courseRepository,
                                CurrentUserContext currentUserContext) {
        this.evaluationRepository = evaluationRepository;
        this.evaluationTypeRepository = evaluationTypeRepository;
        this.sectionRepository = sectionRepository;
        this.courseRepository = courseRepository;
        this.currentUserContext = currentUserContext;
    }

    @GetMapping("/section/{sectionId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'DIRECTOR')")
    public ResponseEntity<List<EvaluationDto>> getBySection(@PathVariable UUID sectionId, Authentication auth) {
        requireSectionInOwnInstitution(sectionId, auth);
        var evaluations = evaluationRepository.findBySectionId(sectionId);
        Map<UUID, EvaluationTypeJpaEntity> typeCache = new HashMap<>();
        var result = evaluations.stream()
            .map(e -> toDto(e, typeCache))
            .toList();
        return ResponseEntity.ok(result);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'DIRECTOR')")
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody CreateEvaluationRequest request, Authentication auth) {
        requireSectionInOwnInstitution(request.sectionId(), auth);
        var now = Instant.now();
        var evaluation = new Evaluation(
            UUID.randomUUID(), request.sectionId(), request.evaluationTypeId(),
            request.name(), request.date(), request.maxScore(), now, now);
        var saved = evaluationRepository.save(evaluation);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(Map.of("evaluationId", saved.getId()));
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

    @GetMapping("/types")
    @PreAuthorize("hasAnyRole('TEACHER', 'DIRECTOR')")
    public ResponseEntity<List<EvaluationTypeDto>> getTypes() {
        var types = evaluationTypeRepository.findAll().stream()
            .map(t -> new EvaluationTypeDto(t.getId(), t.getName(), t.getWeightPercentage()))
            .toList();
        return ResponseEntity.ok(types);
    }

    private EvaluationDto toDto(Evaluation e, Map<UUID, EvaluationTypeJpaEntity> typeCache) {
        var type = typeCache.computeIfAbsent(e.getEvaluationTypeId(),
            id -> evaluationTypeRepository.findById(id).orElse(null));
        return new EvaluationDto(
            e.getId(), e.getSectionId(), e.getEvaluationTypeId(),
            type != null ? type.getName() : "Unknown",
            e.getName(), e.getDate(), e.getMaxScore(),
            e.getCreatedAt(), e.getUpdatedAt());
    }
}
