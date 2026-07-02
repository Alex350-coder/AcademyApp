package com.academicsaas.academic.infrastructure.mapper;

import com.academicsaas.academic.domain.model.Attendance;
import com.academicsaas.academic.domain.model.Course;
import com.academicsaas.academic.domain.model.CourseSection;
import com.academicsaas.academic.domain.model.Enrollment;
import com.academicsaas.academic.domain.model.Evaluation;
import com.academicsaas.academic.domain.model.Grade;
import com.academicsaas.academic.domain.model.valueobject.AttendanceStatus;
import com.academicsaas.academic.domain.model.valueobject.EnrollmentStatus;
import com.academicsaas.academic.domain.model.valueobject.Score;
import com.academicsaas.academic.infrastructure.entity.AttendanceJpaEntity;
import com.academicsaas.academic.infrastructure.entity.CourseJpaEntity;
import com.academicsaas.academic.infrastructure.entity.CourseSectionJpaEntity;
import com.academicsaas.academic.infrastructure.entity.EnrollmentJpaEntity;
import com.academicsaas.academic.infrastructure.entity.EvaluationJpaEntity;
import com.academicsaas.academic.infrastructure.entity.GradeJpaEntity;
import java.math.BigDecimal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AcademicMapper {

    @Mapping(target = "institutionId", source = "institutionId")
    CourseJpaEntity toJpa(Course course);

    default Course toDomain(CourseJpaEntity entity) {
        return new Course(entity.getId(), entity.getName(), entity.getCode(),
            entity.getDescription(), entity.getCredits(), entity.getInstitutionId(),
            entity.getCreatedAt(), entity.getUpdatedAt());
    }

    CourseSectionJpaEntity toJpa(CourseSection section);

    default CourseSection toDomain(CourseSectionJpaEntity entity) {
        return new CourseSection(entity.getId(), entity.getCourseId(),
            entity.getAcademicPeriodId(), entity.getTeacherId(),
            entity.getClassroomId(), entity.getName(), entity.getCapacity(),
            0, entity.getCreatedAt(), entity.getUpdatedAt());
    }

    EnrollmentJpaEntity toJpa(Enrollment enrollment);

    default Enrollment toDomain(EnrollmentJpaEntity entity) {
        return new Enrollment(entity.getId(), entity.getStudentId(),
            entity.getSectionId(), EnrollmentStatus.valueOf(entity.getStatus()),
            entity.getEnrolledAt(), entity.getWithdrawnAt(),
            entity.getCreatedAt(), entity.getUpdatedAt());
    }

    AttendanceJpaEntity toJpa(Attendance attendance);

    default Attendance toDomain(AttendanceJpaEntity entity) {
        return new Attendance(entity.getId(), entity.getEnrollmentId(),
            entity.getDate(), AttendanceStatus.valueOf(entity.getStatus()),
            entity.getJustification(),
            entity.getCreatedAt(), entity.getUpdatedAt());
    }

    EvaluationJpaEntity toJpa(Evaluation evaluation);

    default Evaluation toDomain(EvaluationJpaEntity entity) {
        return new Evaluation(entity.getId(), entity.getSectionId(),
            entity.getEvaluationTypeId(), entity.getName(),
            entity.getDate(), entity.getMaxScore(),
            entity.getCreatedAt(), entity.getUpdatedAt());
    }

    default GradeJpaEntity toJpa(Grade grade) {
        GradeJpaEntity entity = new GradeJpaEntity();
        entity.setId(grade.getId());
        entity.setEvaluationId(grade.getEvaluationId());
        entity.setStudentId(grade.getStudentId());
        entity.setScore(grade.getScore().value());
        entity.setComments(grade.getComments());
        entity.setGradedBy(grade.getGradedBy());
        entity.setGradedAt(grade.getGradedAt());
        entity.setCreatedAt(grade.getCreatedAt());
        entity.setUpdatedAt(grade.getUpdatedAt());
        return entity;
    }

    default Grade toDomain(GradeJpaEntity entity, BigDecimal maxScore) {
        return new Grade(entity.getId(), entity.getEvaluationId(),
            entity.getStudentId(), Score.of(entity.getScore(), maxScore),
            entity.getComments(), entity.getGradedBy(), entity.getGradedAt(),
            entity.getCreatedAt(), entity.getUpdatedAt());
    }

    default Grade toDomain(GradeJpaEntity entity) {
        return toDomain(entity, BigDecimal.valueOf(100));
    }
}
