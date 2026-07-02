package com.academicsaas.identity.infrastructure.persistence;

import com.academicsaas.identity.domain.model.Role;
import com.academicsaas.identity.domain.repository.RoleRepository;
import com.academicsaas.identity.infrastructure.persistence.entity.RoleJpaEntity;
import com.academicsaas.identity.infrastructure.persistence.repository.SpringDataRoleRepository;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class RoleRepositoryAdapter implements RoleRepository {

    private final SpringDataRoleRepository springDataRoleRepository;

    public RoleRepositoryAdapter(SpringDataRoleRepository springDataRoleRepository) {
        this.springDataRoleRepository = springDataRoleRepository;
    }

    @Override
    public Role save(Role role) {
        var jpa = toJpa(role);
        var saved = springDataRoleRepository.save(jpa);
        return toDomain(saved);
    }

    @Override
    public Optional<Role> findById(UUID id) {
        return springDataRoleRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Role> findByName(String name) {
        return springDataRoleRepository.findByName(name).map(this::toDomain);
    }

    @Override
    public Set<Role> findAll() {
        return springDataRoleRepository.findAll().stream()
            .map(this::toDomain)
            .collect(Collectors.toSet());
    }

    private RoleJpaEntity toJpa(Role domain) {
        var jpa = new RoleJpaEntity();
        jpa.setId(domain.getId());
        jpa.setName(domain.getName());
        jpa.setDescription(domain.getDescription());
        return jpa;
    }

    private Role toDomain(RoleJpaEntity jpa) {
        return new Role(
            jpa.getId(),
            jpa.getName(),
            jpa.getDescription(),
            Collections.emptySet()
        );
    }
}
