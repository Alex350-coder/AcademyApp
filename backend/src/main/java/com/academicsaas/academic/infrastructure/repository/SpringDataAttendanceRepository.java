package com.academicsaas.academic.infrastructure.repository;

import com.academicsaas.academic.infrastructure.entity.AttendanceJpaEntity;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataAttendanceRepository extends JpaRepository<AttendanceJpaEntity, UUID> {

    List<AttendanceJpaEntity> findByEnrollmentId(UUID enrollmentId);

    List<AttendanceJpaEntity> findByEnrollmentIdIn(List<UUID> enrollmentIds);

    Optional<AttendanceJpaEntity> findByEnrollmentIdAndDate(UUID enrollmentId, LocalDate date);
}
