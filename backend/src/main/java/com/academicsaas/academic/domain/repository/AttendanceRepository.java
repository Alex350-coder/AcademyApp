package com.academicsaas.academic.domain.repository;

import com.academicsaas.academic.domain.model.Attendance;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AttendanceRepository {
    Attendance save(Attendance attendance);
    List<Attendance> saveAll(List<Attendance> attendances);
    Optional<Attendance> findById(UUID id);
    List<Attendance> findByEnrollmentId(UUID enrollmentId);
    List<Attendance> findByEnrollmentIds(List<UUID> enrollmentIds);
    Optional<Attendance> findByEnrollmentIdAndDate(UUID enrollmentId, LocalDate date);
}
