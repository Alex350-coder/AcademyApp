package com.academicsaas.academic.presentation.controller;

import com.academicsaas.academic.infrastructure.entity.AcademicPeriodJpaEntity;
import com.academicsaas.academic.infrastructure.repository.SpringDataAcademicPeriodRepository;
import com.academicsaas.academic.presentation.dto.PeriodDto;
import com.academicsaas.shared.exception.NotFoundException;
import com.academicsaas.shared.security.CurrentUserContext;
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
@RequestMapping("/api/v1/periods")
public class PeriodController {

    private final SpringDataAcademicPeriodRepository periodRepository;
    private final CurrentUserContext currentUserContext;

    public PeriodController(SpringDataAcademicPeriodRepository periodRepository, CurrentUserContext currentUserContext) {
        this.periodRepository = periodRepository;
        this.currentUserContext = currentUserContext;
    }

    @GetMapping
    public ResponseEntity<List<PeriodDto>> listAll(Authentication auth) {
        var institutionId = currentUserContext.institutionId(auth);
        return ResponseEntity.ok(periodRepository.findByInstitutionId(institutionId).stream().map(this::toDto).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PeriodDto> getById(@PathVariable UUID id, Authentication auth) {
        var entity = findOwnedPeriod(id, auth);
        return ResponseEntity.ok(toDto(entity));
    }

    @PostMapping
    @PreAuthorize("hasRole('DIRECTOR')")
    public ResponseEntity<PeriodDto> create(@RequestBody AcademicPeriodJpaEntity request, Authentication auth) {
        var now = Instant.now();
        var entity = new AcademicPeriodJpaEntity(
            UUID.randomUUID(), request.getName(), request.getStartDate(),
            request.getEndDate(), request.getStatus(),
            currentUserContext.institutionId(auth), now, now);
        var saved = periodRepository.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(saved));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DIRECTOR')")
    public ResponseEntity<PeriodDto> update(@PathVariable UUID id,
                                             @RequestBody AcademicPeriodJpaEntity request,
                                             Authentication auth) {
        var entity = findOwnedPeriod(id, auth);
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
    public ResponseEntity<Void> delete(@PathVariable UUID id, Authentication auth) {
        var entity = findOwnedPeriod(id, auth);
        periodRepository.delete(entity);
        return ResponseEntity.noContent().build();
    }

    private AcademicPeriodJpaEntity findOwnedPeriod(UUID id, Authentication auth) {
        var entity = periodRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("AcademicPeriod", id));
        if (!entity.getInstitutionId().equals(currentUserContext.institutionId(auth))) {
            throw new NotFoundException("AcademicPeriod", id);
        }
        return entity;
    }

    private PeriodDto toDto(AcademicPeriodJpaEntity e) {
        return new PeriodDto(e.getId(), e.getName(), e.getStartDate(), e.getEndDate(),
            e.getStatus(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
