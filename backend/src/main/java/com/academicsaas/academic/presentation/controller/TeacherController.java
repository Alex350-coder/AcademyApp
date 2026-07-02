package com.academicsaas.academic.presentation.controller;

import com.academicsaas.academic.infrastructure.adapter.CourseSectionRepositoryAdapter;
import com.academicsaas.academic.infrastructure.entity.TeacherJpaEntity;
import com.academicsaas.academic.infrastructure.repository.SpringDataTeacherRepository;
import com.academicsaas.academic.presentation.dto.SectionDto;
import com.academicsaas.academic.presentation.dto.TeacherDto;
import com.academicsaas.academic.presentation.dto.TeacherListDto;
import com.academicsaas.identity.infrastructure.persistence.entity.UserJpaEntity;
import com.academicsaas.identity.infrastructure.persistence.repository.SpringDataUserRepository;
import com.academicsaas.shared.exception.NotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/teachers")
public class TeacherController {

    private final SpringDataTeacherRepository teacherRepository;
    private final CourseSectionRepositoryAdapter sectionRepository;
    private final SpringDataUserRepository userRepository;

    public TeacherController(SpringDataTeacherRepository teacherRepository,
                             CourseSectionRepositoryAdapter sectionRepository,
                             SpringDataUserRepository userRepository) {
        this.teacherRepository = teacherRepository;
        this.sectionRepository = sectionRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    @PreAuthorize("hasRole('DIRECTOR')")
    public ResponseEntity<List<TeacherListDto>> getAll() {
        var teachers = teacherRepository.findAll();
        Map<UUID, UserJpaEntity> userCache = new HashMap<>();
        var result = teachers.stream().map(t -> {
            var user = userCache.computeIfAbsent(t.getUserId(),
                id -> userRepository.findById(id).orElse(null));
            return new TeacherListDto(
                t.getId(),
                user != null ? (user.getFirstName() + " " + user.getLastName()).trim() : "Unknown",
                user != null ? user.getEmail() : "unknown@email.com",
                t.getSpecialty() != null ? t.getSpecialty() : "",
                t.getHireDate(),
                user != null ? user.getStatus() : "INACTIVE");
        }).toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/me")
    public ResponseEntity<TeacherDto> getMe(Authentication auth) {
        var userId = UUID.fromString(auth.getName());
        var teacher = teacherRepository.findByUserId(userId)
            .orElseThrow(() -> new NotFoundException("Teacher profile for user", userId));
        return ResponseEntity.ok(toDto(teacher));
    }

    @GetMapping("/me/sections")
    public ResponseEntity<List<SectionDto>> getMySections(Authentication auth) {
        var userId = UUID.fromString(auth.getName());
        var teacher = teacherRepository.findByUserId(userId)
            .orElseThrow(() -> new NotFoundException("Teacher profile for user", userId));
        var sections = sectionRepository.findByTeacherId(teacher.getId());
        return ResponseEntity.ok(sections.stream()
            .map(s -> new SectionDto(
                s.getId(), s.getCourseId(), s.getAcademicPeriodId(),
                s.getTeacherId(), s.getClassroomId(),
                s.getName(), s.getCapacity(), s.getEnrolledCount(),
                null, null,
                s.getCreatedAt(), s.getUpdatedAt()))
            .toList());
    }

    private TeacherDto toDto(TeacherJpaEntity e) {
        return new TeacherDto(e.getId(), e.getUserId(), e.getSpecialty(), e.getHireDate(),
            e.getCreatedAt(), e.getUpdatedAt());
    }
}
