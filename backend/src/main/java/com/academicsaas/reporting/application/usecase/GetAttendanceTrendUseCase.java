package com.academicsaas.reporting.application.usecase;

import com.academicsaas.reporting.application.port.AttendanceReportRepository;
import com.academicsaas.reporting.domain.model.AttendanceTrendData;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class GetAttendanceTrendUseCase {
    private final AttendanceReportRepository attendanceReportRepository;
    public GetAttendanceTrendUseCase(AttendanceReportRepository attendanceReportRepository) {
        this.attendanceReportRepository = attendanceReportRepository;
    }
    public List<AttendanceTrendData> execute(UUID academicPeriodId, LocalDate from, LocalDate to) {
        return attendanceReportRepository.getAttendanceTrend(academicPeriodId, from, to);
    }
}
