package com.academicsaas.academic.presentation.controller;

import com.academicsaas.academic.infrastructure.entity.ClassroomJpaEntity;
import com.academicsaas.academic.infrastructure.repository.SpringDataClassroomRepository;
import com.academicsaas.academic.presentation.dto.ClassroomDto;
import com.academicsaas.academic.presentation.dto.CreateClassroomRequest;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/classrooms")
public class ClassroomController {

    private final SpringDataClassroomRepository classroomRepository;

    public ClassroomController(SpringDataClassroomRepository classroomRepository) {
        this.classroomRepository = classroomRepository;
    }

    @GetMapping
    public ResponseEntity<List<ClassroomDto>> listAll() {
        return ResponseEntity.ok(classroomRepository.findAll().stream().map(this::toDto).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClassroomDto> getById(@PathVariable UUID id) {
        var entity = classroomRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Classroom", id));
        return ResponseEntity.ok(toDto(entity));
    }

    @PostMapping
    @PreAuthorize("hasRole('DIRECTOR')")
    public ResponseEntity<ClassroomDto> create(@Valid @RequestBody CreateClassroomRequest request) {
        var now = Instant.now();
        var entity = new ClassroomJpaEntity(
            UUID.randomUUID(), request.name(), request.code(),
            request.capacity(), request.location(), request.resources(),
            request.institutionId(), now, now);
        var saved = classroomRepository.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(saved));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DIRECTOR')")
    public ResponseEntity<ClassroomDto> update(@PathVariable UUID id,
                                                @Valid @RequestBody CreateClassroomRequest request) {
        var entity = classroomRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Classroom", id));
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
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        var entity = classroomRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Classroom", id));
        classroomRepository.delete(entity);
        return ResponseEntity.noContent().build();
    }

    private ClassroomDto toDto(ClassroomJpaEntity e) {
        return new ClassroomDto(e.getId(), e.getName(), e.getCode(), e.getCapacity(),
            e.getLocation(), e.getResources(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
