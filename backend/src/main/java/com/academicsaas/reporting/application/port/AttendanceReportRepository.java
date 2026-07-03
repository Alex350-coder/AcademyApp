package com.academicsaas.reporting.application.port;

import com.academicsaas.reporting.domain.model.AttendanceTrendData;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AttendanceReportRepository {
    List<AttendanceTrendData> getAttendanceTrend(UUID institutionId, UUID academicPeriodId, LocalDate from, LocalDate to);
}
