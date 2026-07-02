package com.academicsaas.identity.domain.repository;

import com.academicsaas.identity.domain.model.Institution;
import com.academicsaas.identity.domain.model.valueobject.InstitutionId;
import java.util.List;
import java.util.Optional;

public interface InstitutionRepository {

    Institution save(Institution institution);

    Optional<Institution> findById(InstitutionId id);

    Optional<Institution> findByCode(String code);

    List<Institution> findAllActive();

    List<Institution> findAll();

    boolean existsByCode(String code);
}
