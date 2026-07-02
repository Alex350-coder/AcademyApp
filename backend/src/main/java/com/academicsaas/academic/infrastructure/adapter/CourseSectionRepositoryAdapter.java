package com.academicsaas.academic.infrastructure.adapter;

import com.academicsaas.academic.domain.model.CourseSection;
import com.academicsaas.academic.domain.repository.CourseSectionRepository;
import com.academicsaas.academic.infrastructure.entity.CourseSectionJpaEntity;
import com.academicsaas.academic.infrastructure.mapper.AcademicMapper;
import com.academicsaas.academic.infrastructure.repository.SpringDataCourseSectionRepository;
import com.academicsaas.academic.infrastructure.repository.SpringDataEnrollmentRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class CourseSectionRepositoryAdapter implements CourseSectionRepository {

    private final SpringDataCourseSectionRepository jpaRepository;
    private final SpringDataEnrollmentRepository enrollmentRepository;
    private final AcademicMapper mapper;

    public CourseSectionRepositoryAdapter(SpringDataCourseSectionRepository jpaRepository,
                                          SpringDataEnrollmentRepository enrollmentRepository,
                                          AcademicMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.mapper = mapper;
    }

    @Override
    public CourseSection save(CourseSection section) {
        var entity = mapper.toJpa(section);
        var saved = jpaRepository.save(entity);
        return withEnrolledCount(mapper.toDomain(saved), saved.getId());
    }

    @Override
    public Optional<CourseSection> findById(UUID id) {
        return jpaRepository.findById(id)
            .map(entity -> withEnrolledCount(mapper.toDomain(entity), entity.getId()));
    }

    public List<CourseSection> findAll() {
        return jpaRepository.findAll().stream()
            .map(this::toDomainWithCount)
            .toList();
    }

    public List<CourseSection> findByTeacherId(UUID teacherId) {
        return jpaRepository.findByTeacherId(teacherId).stream()
            .map(this::toDomainWithCount)
            .toList();
    }

    public List<CourseSection> findByAcademicPeriodId(UUID periodId) {
        return jpaRepository.findByAcademicPeriodId(periodId).stream()
            .map(this::toDomainWithCount)
            .toList();
    }

    private CourseSection toDomainWithCount(CourseSectionJpaEntity entity) {
        return withEnrolledCount(mapper.toDomain(entity), entity.getId());
    }

    private CourseSection withEnrolledCount(CourseSection section, UUID sectionId) {
        int count = (int) enrollmentRepository.countBySectionId(sectionId);
        return new CourseSection(section.getId(), section.getCourseId(),
            section.getAcademicPeriodId(), section.getTeacherId(),
            section.getClassroomId(), section.getName(), section.getCapacity(),
            count, section.getCreatedAt(), section.getUpdatedAt());
    }
}
