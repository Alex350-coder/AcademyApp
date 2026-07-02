package com.academicsaas.academic.domain.model.valueobject;

import com.academicsaas.shared.exception.ValidationException;
import java.math.BigDecimal;
import java.util.Objects;

public record Score(BigDecimal value, BigDecimal maxScore) {

    public Score {
        Objects.requireNonNull(value, "Score value must not be null");
        Objects.requireNonNull(maxScore, "Max score must not be null");

        if (maxScore.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Max score must be greater than zero");
        }
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Score cannot be negative");
        }
        if (value.compareTo(maxScore) > 0) {
            throw new ValidationException("Score cannot exceed max score of " + maxScore);
        }
    }

    public static Score of(BigDecimal value, BigDecimal maxScore) {
        return new Score(value, maxScore);
    }

    public static Score of(double value, double maxScore) {
        return new Score(BigDecimal.valueOf(value), BigDecimal.valueOf(maxScore));
    }

    public BigDecimal percentage() {
        if (maxScore.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return value.divide(maxScore, 4, java.math.RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100));
    }
}
