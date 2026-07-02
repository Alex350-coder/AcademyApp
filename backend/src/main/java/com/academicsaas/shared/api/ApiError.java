package com.academicsaas.shared.api;

import java.time.Instant;
import java.util.List;

public record ApiError(
    String errorCode,
    String message,
    int status,
    Instant timestamp,
    List<String> details
) {
    public ApiError(String errorCode, String message, int status) {
        this(errorCode, message, status, Instant.now(), List.of());
    }

    public ApiError(String errorCode, String message, int status, List<String> details) {
        this(errorCode, message, status, Instant.now(), details);
    }

    public static ApiError of(String message, int status) {
        return new ApiError("GENERIC_ERROR", message, status);
    }

    public static ApiError notFound(String message) {
        return new ApiError("NOT_FOUND", message, 404);
    }

    public static ApiError validation(String message, List<String> details) {
        return new ApiError("VALIDATION_ERROR", message, 400, details);
    }

    public static ApiError unauthorized(String message) {
        return new ApiError("UNAUTHORIZED", message, 401);
    }

    public static ApiError forbidden(String message) {
        return new ApiError("FORBIDDEN", message, 403);
    }
}
