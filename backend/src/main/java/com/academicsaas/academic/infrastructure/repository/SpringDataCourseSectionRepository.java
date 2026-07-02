package com.academicsaas.academic.infrastructure.repository;

import com.academicsaas.academic.infrastructure.entity.CourseSectionJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataCourseSectionRepository extends JpaRepository<CourseSectionJpaEntity, UUID> {

    List<CourseSectionJpaEntity> findByTeacherId(UUID teacherId);

    List<CourseSectionJpaEntity> findByAcademicPeriodId(UUID academicPeriodId);

    long countByCourseId(UUID courseId);
}
