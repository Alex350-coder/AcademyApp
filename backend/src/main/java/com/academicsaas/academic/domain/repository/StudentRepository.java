package com.academicsaas.academic.domain.repository;

import com.academicsaas.academic.domain.model.Student;
import java.util.Optional;
import java.util.UUID;

public interface StudentRepository {
    Student save(Student student);
    Optional<Student> findById(UUID id);
    Optional<Student> findByUserId(UUID userId);
}
