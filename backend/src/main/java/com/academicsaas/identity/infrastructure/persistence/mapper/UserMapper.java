package com.academicsaas.identity.infrastructure.persistence.mapper;

import com.academicsaas.identity.domain.model.Role;
import com.academicsaas.identity.domain.model.User;
import com.academicsaas.identity.domain.model.valueobject.Email;
import com.academicsaas.identity.domain.model.valueobject.InstitutionId;
import com.academicsaas.identity.domain.model.valueobject.UserId;
import com.academicsaas.identity.domain.model.valueobject.UserStatus;
import com.academicsaas.identity.infrastructure.persistence.entity.RoleJpaEntity;
import com.academicsaas.identity.infrastructure.persistence.entity.UserJpaEntity;
import java.util.Collections;
import java.util.stream.Collectors;

public class UserMapper {

    public UserJpaEntity toJpa(User domain) {
        var jpa = new UserJpaEntity();
        jpa.setId(domain.getId().value());
        jpa.setEmail(domain.getEmail().value());
        jpa.setPasswordHash(domain.getPasswordHash());
        jpa.setFirstName(domain.getFirstName());
        jpa.setLastName(domain.getLastName());
        jpa.setPhone(domain.getPhone());
        jpa.setStatus(domain.getStatus().name());
        jpa.setLastLoginAt(domain.getLastLoginAt());
        if (domain.getInstitutionId() != null) {
            jpa.setInstitutionId(domain.getInstitutionId().value());
        }
        jpa.setRoles(domain.getRoles().stream()
            .map(this::toJpaRole)
            .collect(Collectors.toSet()));
        jpa.setCreatedAt(domain.getCreatedAt());
        jpa.setUpdatedAt(domain.getUpdatedAt());
        return jpa;
    }

    public User toDomain(UserJpaEntity jpa) {
        return new User(
            new UserId(jpa.getId()),
            new Email(jpa.getEmail()),
            jpa.getPasswordHash(),
            jpa.getFirstName(),
            jpa.getLastName(),
            jpa.getPhone(),
            UserStatus.valueOf(jpa.getStatus()),
            jpa.getInstitutionId() != null ? new InstitutionId(jpa.getInstitutionId()) : null,
            jpa.getRoles() != null
                ? jpa.getRoles().stream().map(this::toDomainRole).collect(Collectors.toSet())
                : Collections.emptySet(),
            jpa.getLastLoginAt(),
            jpa.getCreatedAt(),
            jpa.getUpdatedAt()
        );
    }

    RoleJpaEntity toJpaRole(Role domain) {
        var jpa = new RoleJpaEntity();
        jpa.setId(domain.getId());
        jpa.setName(domain.getName());
        jpa.setDescription(domain.getDescription());
        return jpa;
    }

    Role toDomainRole(RoleJpaEntity jpa) {
        return new Role(
            jpa.getId(),
            jpa.getName(),
            jpa.getDescription(),
            Collections.emptySet()
        );
    }
}
