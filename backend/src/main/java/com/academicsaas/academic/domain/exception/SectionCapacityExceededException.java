package com.academicsaas.academic.domain.exception;

import com.academicsaas.shared.exception.DomainException;
import java.util.UUID;

public class SectionCapacityExceededException extends DomainException {

    public SectionCapacityExceededException(UUID sectionId, int capacity) {
        super(
            "Section %s has reached maximum capacity (%d)".formatted(sectionId, capacity),
            "SECTION_CAPACITY_EXCEEDED"
        );
    }
}
