package com.academicsaas.academic.infrastructure.adapter;

import com.academicsaas.academic.domain.model.Course;
import com.academicsaas.academic.domain.repository.CourseRepository;
import com.academicsaas.academic.infrastructure.mapper.AcademicMapper;
import com.academicsaas.academic.infrastructure.repository.SpringDataCourseRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class CourseRepositoryAdapter implements CourseRepository {

    private final SpringDataCourseRepository jpaRepository;
    private final AcademicMapper mapper;

    public CourseRepositoryAdapter(SpringDataCourseRepository jpaRepository, AcademicMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Course save(Course course) {
        var entity = mapper.toJpa(course);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Course> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Course> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Course> findByInstitutionId(UUID institutionId) {
        return jpaRepository.findByInstitutionId(institutionId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public void delete(Course course) {
        jpaRepository.findById(course.getId()).ifPresent(jpaRepository::delete);
    }
}
