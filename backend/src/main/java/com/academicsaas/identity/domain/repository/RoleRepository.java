package com.academicsaas.identity.domain.repository;

import com.academicsaas.identity.domain.model.Role;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface RoleRepository {

    Role save(Role role);

    Optional<Role> findById(UUID id);

    Optional<Role> findByName(String name);

    Set<Role> findAll();
}
