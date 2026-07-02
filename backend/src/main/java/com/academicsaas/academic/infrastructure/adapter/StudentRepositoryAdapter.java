package com.academicsaas.academic.infrastructure.adapter;

import com.academicsaas.academic.domain.model.Student;
import com.academicsaas.academic.domain.repository.StudentRepository;
import com.academicsaas.academic.infrastructure.entity.StudentJpaEntity;
import com.academicsaas.academic.infrastructure.repository.SpringDataStudentRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class StudentRepositoryAdapter implements StudentRepository {

    private final SpringDataStudentRepository jpaRepository;

    public StudentRepositoryAdapter(SpringDataStudentRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Student save(Student student) {
        var entity = toJpa(student);
        var saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Student> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Student> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).map(this::toDomain);
    }

    private Student toDomain(StudentJpaEntity e) {
        return new Student(e.getId(), e.getUserId(), e.getEnrollmentCode(),
            e.getBirthDate(), e.getGuardianName(), e.getGuardianContact(),
            e.getCreatedAt(), e.getUpdatedAt(), e.getDeletedAt());
    }

    private StudentJpaEntity toJpa(Student s) {
        return new StudentJpaEntity(s.getId(), s.getUserId(), s.getEnrollmentCode(),
            s.getBirthDate(), s.getGuardianName(), s.getGuardianContact(),
            s.getCreatedAt(), s.getUpdatedAt(), s.getDeletedAt());
    }
}
