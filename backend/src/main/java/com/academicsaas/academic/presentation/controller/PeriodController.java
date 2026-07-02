package com.academicsaas.academic.presentation.controller;

import com.academicsaas.academic.infrastructure.entity.AcademicPeriodJpaEntity;
import com.academicsaas.academic.infrastructure.repository.SpringDataAcademicPeriodRepository;
import com.academicsaas.academic.presentation.dto.PeriodDto;
import com.academicsaas.shared.exception.NotFoundException;
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
@RequestMapping("/api/v1/periods")
public class PeriodController {

    private final SpringDataAcademicPeriodRepository periodRepository;

    public PeriodController(SpringDataAcademicPeriodRepository periodRepository) {
        this.periodRepository = periodRepository;
    }

    @GetMapping
    public ResponseEntity<List<PeriodDto>> listAll() {
        return ResponseEntity.ok(periodRepository.findAll().stream().map(this::toDto).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PeriodDto> getById(@PathVariable UUID id) {
        var entity = periodRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("AcademicPeriod", id));
        return ResponseEntity.ok(toDto(entity));
    }

    @PostMapping
    @PreAuthorize("hasRole('DIRECTOR')")
    public ResponseEntity<PeriodDto> create(@RequestBody AcademicPeriodJpaEntity request) {
        var now = Instant.now();
        var entity = new AcademicPeriodJpaEntity(
            UUID.randomUUID(), request.getName(), request.getStartDate(),
            request.getEndDate(), request.getStatus(),
            request.getInstitutionId(), now, now);
        var saved = periodRepository.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(saved));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DIRECTOR')")
    public ResponseEntity<PeriodDto> update(@PathVariable UUID id,
                                             @RequestBody AcademicPeriodJpaEntity request) {
        var entity = periodRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("AcademicPeriod", id));
        entity.setName(request.getName());
        entity.setStartDate(request.getStartDate());
        entity.setEndDate(request.getEndDate());
        entity.setStatus(request.getStatus());
        entity.setUpdatedAt(Instant.now());
        var saved = periodRepository.save(entity);
        return ResponseEntity.ok(toDto(saved));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('DIRECTOR')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        var entity = periodRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("AcademicPeriod", id));
        periodRepository.delete(entity);
        return ResponseEntity.noContent().build();
    }

    private PeriodDto toDto(AcademicPeriodJpaEntity e) {
        return new PeriodDto(e.getId(), e.getName(), e.getStartDate(), e.getEndDate(),
            e.getStatus(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
