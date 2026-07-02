package com.academicsaas.academic.infrastructure.adapter;

import com.academicsaas.academic.domain.model.Grade;
import com.academicsaas.academic.domain.model.valueobject.Score;
import com.academicsaas.academic.domain.repository.GradeRepository;
import com.academicsaas.academic.infrastructure.entity.EvaluationJpaEntity;
import com.academicsaas.academic.infrastructure.entity.GradeJpaEntity;
import com.academicsaas.academic.infrastructure.mapper.AcademicMapper;
import com.academicsaas.academic.infrastructure.repository.SpringDataEvaluationRepository;
import com.academicsaas.academic.infrastructure.repository.SpringDataGradeRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class GradeRepositoryAdapter implements GradeRepository {

    private final SpringDataGradeRepository jpaRepository;
    private final SpringDataEvaluationRepository evaluationRepository;
    private final AcademicMapper mapper;

    public GradeRepositoryAdapter(SpringDataGradeRepository jpaRepository,
                                  SpringDataEvaluationRepository evaluationRepository,
                                  AcademicMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.evaluationRepository = evaluationRepository;
        this.mapper = mapper;
    }

    @Override
    public Grade save(Grade grade) {
        var entity = mapper.toJpa(grade);
        var saved = jpaRepository.save(entity);
        var maxScore = findMaxScore(saved.getEvaluationId());
        return mapper.toDomain(saved, maxScore);
    }

    @Override
    public Optional<Grade> findById(UUID id) {
        return jpaRepository.findById(id)
            .map(entity -> {
                var maxScore = findMaxScore(entity.getEvaluationId());
                return mapper.toDomain(entity, maxScore);
            });
    }

    @Override
    public Optional<Grade> findByEvaluationIdAndStudentId(UUID evaluationId, UUID studentId) {
        return jpaRepository.findByEvaluationIdAndStudentId(evaluationId, studentId)
            .map(entity -> {
                var maxScore = findMaxScore(evaluationId);
                return mapper.toDomain(entity, maxScore);
            });
    }

    @Override
    public List<Grade> findByStudentIdAndSectionId(UUID studentId, UUID sectionId) {
        var evaluations = evaluationRepository.findBySectionId(sectionId);
        var evaluationIds = evaluations.stream().map(EvaluationJpaEntity::getId).toList();
        var grades = jpaRepository.findByStudentId(studentId).stream()
            .filter(g -> evaluationIds.contains(g.getEvaluationId()))
            .toList();
        return grades.stream()
            .map(g -> {
                var maxScore = evaluations.stream()
                    .filter(e -> e.getId().equals(g.getEvaluationId()))
                    .findFirst()
                    .map(EvaluationJpaEntity::getMaxScore)
                    .orElse(BigDecimal.valueOf(100));
                return mapper.toDomain(g, maxScore);
            })
            .toList();
    }

    public List<Grade> findByStudentId(UUID studentId) {
        return jpaRepository.findByStudentId(studentId).stream()
            .map(entity -> {
                var maxScore = findMaxScore(entity.getEvaluationId());
                return mapper.toDomain(entity, maxScore);
            })
            .toList();
    }

    public List<Grade> findByEvaluationId(UUID evaluationId) {
        var maxScore = findMaxScore(evaluationId);
        return jpaRepository.findByEvaluationId(evaluationId).stream()
            .map(entity -> mapper.toDomain(entity, maxScore))
            .toList();
    }

    private BigDecimal findMaxScore(UUID evaluationId) {
        return evaluationRepository.findById(evaluationId)
            .map(EvaluationJpaEntity::getMaxScore)
            .orElse(BigDecimal.valueOf(100));
    }
}
