package com.academicsaas.academic.infrastructure.adapter;

import com.academicsaas.academic.domain.model.Enrollment;
import com.academicsaas.academic.domain.repository.EnrollmentRepository;
import com.academicsaas.academic.infrastructure.mapper.AcademicMapper;
import com.academicsaas.academic.infrastructure.repository.SpringDataEnrollmentRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class EnrollmentRepositoryAdapter implements EnrollmentRepository {

    private final SpringDataEnrollmentRepository jpaRepository;
    private final AcademicMapper mapper;

    public EnrollmentRepositoryAdapter(SpringDataEnrollmentRepository jpaRepository,
                                       AcademicMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Enrollment save(Enrollment enrollment) {
        var entity = mapper.toJpa(enrollment);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Enrollment> findById(UUID id) {
        return jpaRepository.findById(id)
            .map(mapper::toDomain);
    }

    @Override
    public boolean existsByStudentIdAndSectionId(UUID studentId, UUID sectionId) {
        return jpaRepository.existsByStudentIdAndSectionId(studentId, sectionId);
    }

    @Override
    public List<Enrollment> findAllActive() {
        return jpaRepository.findByStatus("ACTIVE").stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public List<Enrollment> findByStudentId(UUID studentId) {
        return jpaRepository.findByStudentId(studentId).stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public List<Enrollment> findBySectionId(UUID sectionId) {
        return jpaRepository.findBySectionId(sectionId).stream()
            .map(mapper::toDomain)
            .toList();
    }
}
