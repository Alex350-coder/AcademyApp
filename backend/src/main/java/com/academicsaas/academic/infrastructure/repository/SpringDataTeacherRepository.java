package com.academicsaas.academic.infrastructure.repository;

import com.academicsaas.academic.infrastructure.entity.TeacherJpaEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataTeacherRepository extends JpaRepository<TeacherJpaEntity, UUID> {

    Optional<TeacherJpaEntity> findByUserId(UUID userId);
}
