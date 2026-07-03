package com.academicsaas.academic.presentation.controller;

import com.academicsaas.academic.infrastructure.entity.ClassroomJpaEntity;
import com.academicsaas.academic.infrastructure.repository.SpringDataClassroomRepository;
import com.academicsaas.academic.presentation.dto.ClassroomDto;
import com.academicsaas.academic.presentation.dto.CreateClassroomRequest;
import com.academicsaas.shared.exception.NotFoundException;
import com.academicsaas.shared.security.CurrentUserContext;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/classrooms")
public class ClassroomController {

    private final SpringDataClassroomRepository classroomRepository;
    private final CurrentUserContext currentUserContext;

    public ClassroomController(SpringDataClassroomRepository classroomRepository, CurrentUserContext currentUserContext) {
        this.classroomRepository = classroomRepository;
        this.currentUserContext = currentUserContext;
    }

    @GetMapping
    public ResponseEntity<List<ClassroomDto>> listAll(Authentication auth) {
        var institutionId = currentUserContext.institutionId(auth);
        return ResponseEntity.ok(classroomRepository.findByInstitutionId(institutionId).stream().map(this::toDto).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClassroomDto> getById(@PathVariable UUID id, Authentication auth) {
        var entity = findOwnedClassroom(id, auth);
        return ResponseEntity.ok(toDto(entity));
    }

    @PostMapping
    @PreAuthorize("hasRole('DIRECTOR')")
    public ResponseEntity<ClassroomDto> create(@Valid @RequestBody CreateClassroomRequest request, Authentication auth) {
        var now = Instant.now();
        var entity = new ClassroomJpaEntity(
            UUID.randomUUID(), request.name(), request.code(),
            request.capacity(), request.location(), request.resources(),
            currentUserContext.institutionId(auth), now, now);
        var saved = classroomRepository.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(saved));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DIRECTOR')")
    public ResponseEntity<ClassroomDto> update(@PathVariable UUID id,
                                                @Valid @RequestBody CreateClassroomRequest request,
                                                Authentication auth) {
        var entity = findOwnedClassroom(id, auth);
        entity.setName(request.name());
        entity.setCode(request.code());
        entity.setCapacity(request.capacity());
        entity.setLocation(request.location());
        entity.setResources(request.resources());
        entity.setUpdatedAt(Instant.now());
        var saved = classroomRepository.save(entity);
        return ResponseEntity.ok(toDto(saved));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('DIRECTOR')")
    public ResponseEntity<Void> delete(@PathVariable UUID id, Authentication auth) {
        var entity = findOwnedClassroom(id, auth);
        classroomRepository.delete(entity);
        return ResponseEntity.noContent().build();
    }

    private ClassroomJpaEntity findOwnedClassroom(UUID id, Authentication auth) {
        var entity = classroomRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Classroom", id));
        if (!entity.getInstitutionId().equals(currentUserContext.institutionId(auth))) {
            throw new NotFoundException("Classroom", id);
        }
        return entity;
    }

    private ClassroomDto toDto(ClassroomJpaEntity e) {
        return new ClassroomDto(e.getId(), e.getName(), e.getCode(), e.getCapacity(),
            e.getLocation(), e.getResources(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
