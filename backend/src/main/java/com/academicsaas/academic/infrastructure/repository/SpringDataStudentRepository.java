package com.academicsaas.academic.infrastructure.repository;

import com.academicsaas.academic.infrastructure.entity.StudentJpaEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataStudentRepository extends JpaRepository<StudentJpaEntity, UUID> {

    Optional<StudentJpaEntity> findByUserId(UUID userId);
}
