package com.academicsaas.academic.application.port;

import java.util.UUID;

public interface AcademicNotifier {

    void notifyEnrollmentConfirmed(UUID studentId, UUID sectionId);

    void notifyGradePublished(UUID studentId, UUID evaluationId);

    void notifyAtRiskAlert(UUID studentId, UUID sectionId, double average);
}
