package com.academicsaas.identity.infrastructure.persistence.repository;

import com.academicsaas.identity.infrastructure.persistence.entity.RoleJpaEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataRoleRepository extends JpaRepository<RoleJpaEntity, UUID> {

    Optional<RoleJpaEntity> findByName(String name);
}
