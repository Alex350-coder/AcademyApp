package com.academicsaas.academic.presentation.controller;

import com.academicsaas.academic.domain.model.Course;
import com.academicsaas.academic.domain.model.CourseSection;
import com.academicsaas.academic.domain.repository.CourseRepository;
import com.academicsaas.academic.infrastructure.adapter.CourseSectionRepositoryAdapter;
import com.academicsaas.academic.presentation.dto.CreateSectionRequest;
import com.academicsaas.academic.presentation.dto.SectionDto;
import com.academicsaas.shared.exception.NotFoundException;
import com.academicsaas.shared.security.CurrentUserContext;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
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
@RequestMapping("/api/v1/sections")
public class SectionController {

    private final CourseSectionRepositoryAdapter sectionRepository;
    private final CourseRepository courseRepository;
    private final CurrentUserContext currentUserContext;

    public SectionController(CourseSectionRepositoryAdapter sectionRepository,
                             CourseRepository courseRepository,
                             CurrentUserContext currentUserContext) {
        this.sectionRepository = sectionRepository;
        this.courseRepository = courseRepository;
        this.currentUserContext = currentUserContext;
    }

    @GetMapping
    public ResponseEntity<List<SectionDto>> listAll(
            @RequestParam(required = false) UUID periodId,
            @RequestParam(required = false) UUID teacherId,
            Authentication auth) {
        List<CourseSection> sections;
        if (periodId != null) {
            sections = sectionRepository.findByAcademicPeriodId(periodId);
        } else if (teacherId != null) {
            sections = sectionRepository.findByTeacherId(teacherId);
        } else {
            sections = sectionRepository.findAll();
        }
        return ResponseEntity.ok(scopeToOwnInstitution(sections, auth).stream().map(this::toDto).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SectionDto> getById(@PathVariable UUID id, Authentication auth) {
        var section = findOwnedSection(id, auth);
        return ResponseEntity.ok(toDto(section));
    }

    @GetMapping("/available")
    public ResponseEntity<List<SectionDto>> listAvailable(Authentication auth) {
        var sections = sectionRepository.findAll().stream()
            .filter(s -> s.getEnrolledCount() < s.getCapacity())
            .toList();
        return ResponseEntity.ok(scopeToOwnInstitution(sections, auth).stream().map(this::toDto).toList());
    }

    @PostMapping
    @PreAuthorize("hasRole('DIRECTOR')")
    public ResponseEntity<SectionDto> create(@Valid @RequestBody CreateSectionRequest request, Authentication auth) {
        var course = courseRepository.findById(request.courseId())
            .orElseThrow(() -> new NotFoundException("Course", request.courseId()));
        if (!course.getInstitutionId().equals(currentUserContext.institutionId(auth))) {
            throw new NotFoundException("Course", request.courseId());
        }

        var now = Instant.now();
        var section = new CourseSection(
            UUID.randomUUID(),
            request.courseId(),
            request.academicPeriodId(),
            request.teacherId(),
            request.classroomId(),
            request.name(),
            request.capacity(),
            0,
            now, now
        );
        var saved = sectionRepository.save(section);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(saved));
    }

    // CourseSection carries no institutionId of its own - ownership is
    // resolved transitively through its Course.
    private List<CourseSection> scopeToOwnInstitution(List<CourseSection> sections, Authentication auth) {
        var institutionId = currentUserContext.institutionId(auth);
        var ownCourseIds = courseRepository.findByInstitutionId(institutionId).stream()
            .map(Course::getId)
            .collect(Collectors.toSet());
        return sections.stream().filter(s -> ownCourseIds.contains(s.getCourseId())).toList();
    }

    private CourseSection findOwnedSection(UUID id, Authentication auth) {
        var section = sectionRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("CourseSection", id));
        var course = courseRepository.findById(section.getCourseId()).orElse(null);
        if (course == null || !course.getInstitutionId().equals(currentUserContext.institutionId(auth))) {
            throw new NotFoundException("CourseSection", id);
        }
        return section;
    }

    private SectionDto toDto(CourseSection section) {
        var courseName = courseRepository.findById(section.getCourseId())
            .map(c -> c.getName()).orElse(null);
        return new SectionDto(
            section.getId(), section.getCourseId(), section.getAcademicPeriodId(),
            section.getTeacherId(), section.getClassroomId(),
            section.getName(), section.getCapacity(), section.getEnrolledCount(),
            courseName, null,
            section.getCreatedAt(), section.getUpdatedAt());
    }
}
