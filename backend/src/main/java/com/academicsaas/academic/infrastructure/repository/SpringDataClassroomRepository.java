package com.academicsaas.academic.infrastructure.repository;

import com.academicsaas.academic.infrastructure.entity.ClassroomJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataClassroomRepository extends JpaRepository<ClassroomJpaEntity, UUID> {
    List<ClassroomJpaEntity> findByInstitutionId(UUID institutionId);
}
