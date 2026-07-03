package com.academicsaas.academic.presentation.controller;

import com.academicsaas.academic.domain.repository.CourseRepository;
import com.academicsaas.academic.infrastructure.adapter.CourseSectionRepositoryAdapter;
import com.academicsaas.academic.infrastructure.entity.TeacherJpaEntity;
import com.academicsaas.academic.infrastructure.repository.SpringDataTeacherRepository;
import com.academicsaas.academic.presentation.dto.SectionDto;
import com.academicsaas.academic.presentation.dto.TeacherDto;
import com.academicsaas.academic.presentation.dto.TeacherListDto;
import com.academicsaas.identity.infrastructure.persistence.entity.UserJpaEntity;
import com.academicsaas.identity.infrastructure.persistence.repository.SpringDataUserRepository;
import com.academicsaas.shared.exception.NotFoundException;
import com.academicsaas.shared.security.CurrentUserContext;
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
    private final CourseRepository courseRepository;
    private final CurrentUserContext currentUserContext;

    public TeacherController(SpringDataTeacherRepository teacherRepository,
                             CourseSectionRepositoryAdapter sectionRepository,
                             SpringDataUserRepository userRepository,
                             CourseRepository courseRepository,
                             CurrentUserContext currentUserContext) {
        this.teacherRepository = teacherRepository;
        this.sectionRepository = sectionRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.currentUserContext = currentUserContext;
    }

    @GetMapping
    @PreAuthorize("hasRole('DIRECTOR')")
    public ResponseEntity<List<TeacherListDto>> getAll(Authentication auth) {
        var institutionId = currentUserContext.institutionId(auth);
        var teachers = teacherRepository.findAll();
        Map<UUID, UserJpaEntity> userCache = new HashMap<>();
        var result = teachers.stream()
            .map(t -> Map.entry(t, userCache.computeIfAbsent(t.getUserId(),
                id -> userRepository.findById(id).orElse(null))))
            .filter(entry -> entry.getValue() != null && institutionId.equals(entry.getValue().getInstitutionId()))
            .map(entry -> {
                var t = entry.getKey();
                var user = entry.getValue();
                return new TeacherListDto(
                    t.getId(),
                    (user.getFirstName() + " " + user.getLastName()).trim(),
                    user.getEmail(),
                    t.getSpecialty() != null ? t.getSpecialty() : "",
                    t.getHireDate(),
                    user.getStatus());
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
        var teacherUser = userRepository.findById(teacher.getUserId()).orElse(null);
        var teacherName = teacherUser != null
            ? (teacherUser.getFirstName() + " " + teacherUser.getLastName()).trim()
            : null;
        var sections = sectionRepository.findByTeacherId(teacher.getId());
        Map<UUID, String> courseNameCache = new HashMap<>();
        return ResponseEntity.ok(sections.stream()
            .map(s -> {
                var courseName = courseNameCache.computeIfAbsent(s.getCourseId(),
                    id -> courseRepository.findById(id).map(c -> c.getName()).orElse(null));
                return new SectionDto(
                    s.getId(), s.getCourseId(), s.getAcademicPeriodId(),
                    s.getTeacherId(), s.getClassroomId(),
                    s.getName(), s.getCapacity(), s.getEnrolledCount(),
                    courseName, teacherName,
                    s.getCreatedAt(), s.getUpdatedAt());
            })
            .toList());
    }

    private TeacherDto toDto(TeacherJpaEntity e) {
        return new TeacherDto(e.getId(), e.getUserId(), e.getSpecialty(), e.getHireDate(),
            e.getCreatedAt(), e.getUpdatedAt());
    }
}
