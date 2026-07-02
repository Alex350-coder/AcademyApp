package com.academicsaas.academic.infrastructure.repository;

import com.academicsaas.academic.infrastructure.entity.CourseJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataCourseRepository extends JpaRepository<CourseJpaEntity, UUID> {
    List<CourseJpaEntity> findByInstitutionId(UUID institutionId);
}
