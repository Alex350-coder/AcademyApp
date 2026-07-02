package com.academicsaas.academic.infrastructure.adapter;

import com.academicsaas.academic.domain.model.Attendance;
import com.academicsaas.academic.domain.repository.AttendanceRepository;
import com.academicsaas.academic.infrastructure.mapper.AcademicMapper;
import com.academicsaas.academic.infrastructure.repository.SpringDataAttendanceRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class AttendanceRepositoryAdapter implements AttendanceRepository {

    private final SpringDataAttendanceRepository jpaRepository;
    private final AcademicMapper mapper;

    public AttendanceRepositoryAdapter(SpringDataAttendanceRepository jpaRepository,
                                       AcademicMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Attendance save(Attendance attendance) {
        var entity = mapper.toJpa(attendance);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Attendance> findById(UUID id) {
        return jpaRepository.findById(id)
            .map(mapper::toDomain);
    }

    @Override
    public List<Attendance> findByEnrollmentId(UUID enrollmentId) {
        return jpaRepository.findByEnrollmentId(enrollmentId).stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public List<Attendance> findByEnrollmentIds(List<UUID> enrollmentIds) {
        return jpaRepository.findByEnrollmentIdIn(enrollmentIds).stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public Optional<Attendance> findByEnrollmentIdAndDate(UUID enrollmentId, LocalDate date) {
        return jpaRepository.findByEnrollmentIdAndDate(enrollmentId, date)
            .map(mapper::toDomain);
    }

    @Override
    public List<Attendance> saveAll(List<Attendance> attendances) {
        var entities = attendances.stream()
            .map(mapper::toJpa)
            .toList();
        var saved = jpaRepository.saveAll(entities);
        return saved.stream()
            .map(mapper::toDomain)
            .toList();
    }
}
