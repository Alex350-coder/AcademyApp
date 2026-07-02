package com.academicsaas.shared.security;

import com.academicsaas.shared.exception.ValidationException;
import java.util.List;
import java.util.Set;

public class SortFilterValidator {

    private SortFilterValidator() {}

    public static void validateSortField(String sortField, Set<String> allowedFields) {
        if (sortField == null || sortField.isBlank()) return;
        if (!allowedFields.contains(sortField)) {
            throw new ValidationException("Invalid sort field: " + sortField);
        }
    }

    public static void validateFilterFields(List<String> filterFields, Set<String> allowedFields) {
        if (filterFields == null || filterFields.isEmpty()) return;
        for (var field : filterFields) {
            if (!allowedFields.contains(field)) {
                throw new ValidationException("Invalid filter field: " + field);
            }
        }
    }

    public static void validateSortDirection(String direction) {
        if (direction == null || direction.isBlank()) return;
        var dir = direction.toUpperCase();
        if (!"ASC".equals(dir) && !"DESC".equals(dir)) {
            throw new ValidationException("Invalid sort direction: " + direction);
        }
    }

    public static void validatePageSize(int size, int maxSize) {
        if (size < 1) {
            throw new ValidationException("Page size must be at least 1");
        }
        if (size > maxSize) {
            throw new ValidationException("Page size must not exceed " + maxSize);
        }
    }
}
