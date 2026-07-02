package com.academicsaas.academic.domain.model.valueobject;

public enum AttendanceStatus {
    PRESENT,
    ABSENT,
    LATE,
    JUSTIFIED;

    public boolean isPresent() {
        return this == PRESENT;
    }

    public boolean isAbsent() {
        return this == ABSENT;
    }
}
