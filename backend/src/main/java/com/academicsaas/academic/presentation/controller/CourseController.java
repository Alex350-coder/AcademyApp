package com.academicsaas.academic.presentation.controller;

import com.academicsaas.academic.domain.model.Course;
import com.academicsaas.academic.domain.repository.CourseRepository;
import com.academicsaas.academic.presentation.dto.CourseDto;
import com.academicsaas.academic.presentation.dto.CreateCourseRequest;
import com.academicsaas.academic.presentation.dto.UpdateCourseRequest;
import com.academicsaas.shared.exception.NotFoundException;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/courses")
public class CourseController {

    private final CourseRepository courseRepository;

    public CourseController(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @GetMapping
    public ResponseEntity<List<CourseDto>> listAll(@RequestParam(required = false) String search) {
        var courses = courseRepository.findAll();
        if (search != null && !search.isBlank()) {
            var term = search.toLowerCase();
            courses = courses.stream()
                .filter(c -> c.getName().toLowerCase().contains(term)
                          || c.getCode().toLowerCase().contains(term))
                .toList();
        }
        return ResponseEntity.ok(courses.stream().map(this::toDto).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseDto> getById(@PathVariable UUID id) {
        var course = courseRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Course", id));
        return ResponseEntity.ok(toDto(course));
    }

    @PostMapping
    @PreAuthorize("hasRole('DIRECTOR')")
    public ResponseEntity<CourseDto> create(@Valid @RequestBody CreateCourseRequest request) {
        var now = Instant.now();
        var course = new Course(
            UUID.randomUUID(), request.name(), request.code(),
            request.description(), request.credits(),
            request.institutionId(), now, now);
        var saved = courseRepository.save(course);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(saved));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DIRECTOR')")
    public ResponseEntity<CourseDto> update(@PathVariable UUID id,
                                            @Valid @RequestBody UpdateCourseRequest request) {
        var course = courseRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Course", id));

        var updated = new Course(
            course.getId(),
            request.name() != null ? request.name() : course.getName(),
            request.code() != null ? request.code() : course.getCode(),
            request.description() != null ? request.description() : course.getDescription(),
            request.credits() != null ? request.credits() : course.getCredits(),
            course.getInstitutionId(),
            course.getCreatedAt(),
            Instant.now()
        );
        var saved = courseRepository.save(updated);
        return ResponseEntity.ok(toDto(saved));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('DIRECTOR')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        var course = courseRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Course", id));
        courseRepository.delete(course);
        return ResponseEntity.noContent().build();
    }

    private CourseDto toDto(Course course) {
        return new CourseDto(course.getId(), course.getName(), course.getCode(),
            course.getDescription(), course.getCredits(),
            course.getCreatedAt(), course.getUpdatedAt());
    }
}
