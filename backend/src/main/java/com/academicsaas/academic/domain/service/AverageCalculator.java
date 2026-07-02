package com.academicsaas.academic.domain.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class AverageCalculator {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    public record WeightedGrade(BigDecimal score, BigDecimal maxScore, BigDecimal weightPercentage) {}

    public AverageResult calculate(List<WeightedGrade> grades) {
        if (grades == null || grades.isEmpty()) {
            return new AverageResult(BigDecimal.ZERO, 0, 0);
        }

        var totalWeight = grades.stream()
            .map(WeightedGrade::weightPercentage)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        var weightedSum = BigDecimal.ZERO;
        var gradedCount = 0;

        for (var grade : grades) {
            var percentage = grade.score().multiply(HUNDRED)
                .divide(grade.maxScore(), 4, RoundingMode.HALF_UP);

            var weighted = percentage.multiply(grade.weightPercentage())
                .divide(totalWeight, 4, RoundingMode.HALF_UP);

            weightedSum = weightedSum.add(weighted);
            gradedCount++;
        }

        var average = weightedSum.setScale(2, RoundingMode.HALF_UP);
        return new AverageResult(average, gradedCount, grades.size());
    }

    public record AverageResult(BigDecimal average, int gradedEvaluations, int totalEvaluations) {
        public boolean isAtRisk(BigDecimal passingThreshold) {
            return average.compareTo(passingThreshold) < 0;
        }
    }
}
