package com.academicsaas.academic.application.usecase;

import com.academicsaas.academic.domain.model.Attendance;
import com.academicsaas.academic.domain.model.valueobject.AttendanceStatus;
import com.academicsaas.academic.domain.repository.AttendanceRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RegisterAttendanceUseCase {

    private final AttendanceRepository attendanceRepository;

    public RegisterAttendanceUseCase(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    public record Request(UUID enrollmentId, LocalDate date, String status) {}

    public record BulkRequest(UUID sectionId, LocalDate date, List<SingleAttendance> attendances) {}

    public record SingleAttendance(UUID enrollmentId, String status) {}

    public record Response(UUID attendanceId) {}

    public record BulkResponse(int registered) {}

    public Response execute(Request request) {
        var attendanceStatus = AttendanceStatus.valueOf(request.status().toUpperCase());

        var attendance = Attendance.create(
            UUID.randomUUID(),
            request.enrollmentId(),
            request.date(),
            attendanceStatus
        );

        var saved = attendanceRepository.save(attendance);
        return new Response(saved.getId());
    }

    public BulkResponse executeBulk(BulkRequest request) {
        var attendances = request.attendances().stream()
            .map(a -> Attendance.create(
                UUID.randomUUID(),
                a.enrollmentId(),
                request.date(),
                AttendanceStatus.valueOf(a.status().toUpperCase())
            ))
            .toList();

        var saved = attendanceRepository.saveAll(attendances);
        return new BulkResponse(saved.size());
    }
}
