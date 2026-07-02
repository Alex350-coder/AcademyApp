package com.academicsaas.academic.domain.exception;

import com.academicsaas.shared.exception.DomainException;

public class DuplicateEnrollmentException extends DomainException {

    public DuplicateEnrollmentException(String studentId, String sectionId) {
        super(
            "Student %s is already enrolled in section %s".formatted(studentId, sectionId),
            "DUPLICATE_ENROLLMENT"
        );
    }
}
