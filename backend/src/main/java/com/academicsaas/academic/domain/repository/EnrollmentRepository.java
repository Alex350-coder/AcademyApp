package com.academicsaas.academic.domain.repository;

import com.academicsaas.academic.domain.model.Enrollment;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EnrollmentRepository {

    Enrollment save(Enrollment enrollment);

    Optional<Enrollment> findById(UUID id);

    boolean existsByStudentIdAndSectionId(UUID studentId, UUID sectionId);

    List<Enrollment> findAllActive();

    List<Enrollment> findByStudentId(UUID studentId);

    List<Enrollment> findBySectionId(UUID sectionId);
}
