package com.academicsaas.identity.infrastructure.persistence;

import com.academicsaas.identity.domain.model.Institution;
import com.academicsaas.identity.domain.model.valueobject.InstitutionId;
import com.academicsaas.identity.domain.repository.InstitutionRepository;
import com.academicsaas.identity.infrastructure.persistence.entity.InstitutionJpaEntity;
import com.academicsaas.identity.infrastructure.persistence.repository.SpringDataInstitutionRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class InstitutionRepositoryAdapter implements InstitutionRepository {

    private final SpringDataInstitutionRepository springRepo;

    public InstitutionRepositoryAdapter(SpringDataInstitutionRepository springRepo) {
        this.springRepo = springRepo;
    }

    @Override
    public Institution save(Institution domain) {
        var jpa = toJpa(domain);
        var saved = springRepo.save(jpa);
        return toDomain(saved);
    }

    @Override
    public Optional<Institution> findById(InstitutionId id) {
        return springRepo.findById(id.value()).map(this::toDomain);
    }

    @Override
    public Optional<Institution> findByCode(String code) {
        return springRepo.findByCode(code.toUpperCase()).map(this::toDomain);
    }

    @Override
    public List<Institution> findAllActive() {
        return springRepo.findByIsActiveTrue().stream().map(this::toDomain).toList();
    }

    @Override
    public List<Institution> findAll() {
        return springRepo.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public boolean existsByCode(String code) {
        return springRepo.existsByCode(code.toUpperCase());
    }

    private InstitutionJpaEntity toJpa(Institution domain) {
        var jpa = new InstitutionJpaEntity();
        jpa.setId(domain.getId().value());
        jpa.setName(domain.getName());
        jpa.setCode(domain.getCode());
        jpa.setAddress(domain.getAddress());
        jpa.setPhone(domain.getPhone());
        jpa.setEmail(domain.getEmail());
        jpa.setActive(domain.isActive());
        jpa.setCreatedAt(domain.getCreatedAt());
        jpa.setUpdatedAt(domain.getUpdatedAt());
        return jpa;
    }

    private Institution toDomain(InstitutionJpaEntity jpa) {
        return new Institution(
            new InstitutionId(jpa.getId()),
            jpa.getName(),
            jpa.getCode(),
            jpa.getAddress(),
            jpa.getPhone(),
            jpa.getEmail(),
            jpa.isActive(),
            jpa.getCreatedAt(),
            jpa.getUpdatedAt()
        );
    }
}
