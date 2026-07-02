package com.academicsaas.identity.presentation.controller;

import com.academicsaas.identity.domain.model.Institution;
import com.academicsaas.identity.domain.repository.InstitutionRepository;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/institutions")
public class InstitutionController {

    private final InstitutionRepository institutionRepository;

    public InstitutionController(InstitutionRepository institutionRepository) {
        this.institutionRepository = institutionRepository;
    }

    @GetMapping
    public ResponseEntity<List<InstitutionDto>> listActive() {
        var institutions = institutionRepository.findAllActive().stream()
            .map(this::toDto)
            .toList();
        return ResponseEntity.ok(institutions);
    }

    private InstitutionDto toDto(Institution inst) {
        return new InstitutionDto(
            inst.getId().value().toString(),
            inst.getName(),
            inst.getCode(),
            inst.getAddress(),
            inst.getPhone(),
            inst.getEmail()
        );
    }

    public record InstitutionDto(
        String id,
        String name,
        String code,
        String address,
        String phone,
        String email
    ) {}
}
