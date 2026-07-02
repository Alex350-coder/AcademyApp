package com.academicsaas.academic.infrastructure.adapter;

import com.academicsaas.academic.domain.model.Evaluation;
import com.academicsaas.academic.domain.repository.EvaluationRepository;
import com.academicsaas.academic.infrastructure.mapper.AcademicMapper;
import com.academicsaas.academic.infrastructure.repository.SpringDataEvaluationRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class EvaluationRepositoryAdapter implements EvaluationRepository {

    private final SpringDataEvaluationRepository jpaRepository;
    private final AcademicMapper mapper;

    public EvaluationRepositoryAdapter(SpringDataEvaluationRepository jpaRepository,
                                       AcademicMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Evaluation save(Evaluation evaluation) {
        var entity = mapper.toJpa(evaluation);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Evaluation> findById(UUID id) {
        return jpaRepository.findById(id)
            .map(mapper::toDomain);
    }

    @Override
    public List<Evaluation> findBySectionId(UUID sectionId) {
        return jpaRepository.findBySectionId(sectionId).stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public List<Evaluation> findAll() {
        return jpaRepository.findAll().stream()
            .map(mapper::toDomain)
            .toList();
    }
}
