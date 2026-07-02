package com.academicsaas.academic.domain.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("AverageCalculator")
class AverageCalculatorTest {

    private final AverageCalculator calculator = new AverageCalculator();

    @Nested
    @DisplayName("calculate")
    class Calculate {

        @Test
        @DisplayName("should return average 0 and gradedCount 0 when grades list is empty")
        void should_returnZeroAverage_when_emptyGrades() {
            var result = calculator.calculate(Collections.emptyList());

            assertThat(result.average()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(result.gradedEvaluations()).isZero();
            assertThat(result.totalEvaluations()).isZero();
        }

        @Test
        @DisplayName("should return 100.00 when single grade is at 100%")
        void should_returnFullAverage_when_singleGradeAtMax() {
            var grades = List.of(new AverageCalculator.WeightedGrade(
                BigDecimal.valueOf(100), BigDecimal.valueOf(100), BigDecimal.valueOf(100)));

            var result = calculator.calculate(grades);

            assertThat(result.average()).isEqualByComparingTo("100.00");
            assertThat(result.gradedEvaluations()).isEqualTo(1);
        }

        @Test
        @DisplayName("should return 50.00 when single grade is at 50%")
        void should_returnHalfAverage_when_singleGradeAtHalf() {
            var grades = List.of(new AverageCalculator.WeightedGrade(
                BigDecimal.valueOf(50), BigDecimal.valueOf(100), BigDecimal.valueOf(100)));

            var result = calculator.calculate(grades);

            assertThat(result.average()).isEqualByComparingTo("50.00");
        }

        @Test
        @DisplayName("should correctly weight multiple grades with equal weights")
        void should_calculateCorrectAverage_when_equalWeights() {
            var grades = List.of(
                new AverageCalculator.WeightedGrade(
                    BigDecimal.valueOf(80), BigDecimal.valueOf(100), BigDecimal.valueOf(50)),
                new AverageCalculator.WeightedGrade(
                    BigDecimal.valueOf(90), BigDecimal.valueOf(100), BigDecimal.valueOf(50))
            );

            var result = calculator.calculate(grades);

            assertThat(result.average()).isEqualByComparingTo("85.00");
            assertThat(result.gradedEvaluations()).isEqualTo(2);
        }

        @Test
        @DisplayName("should correctly weight grades with different weights")
        void should_calculateWeightedAverage_when_differentWeights() {
            var grades = List.of(
                new AverageCalculator.WeightedGrade(
                    BigDecimal.valueOf(85), BigDecimal.valueOf(100), BigDecimal.valueOf(40)),
                new AverageCalculator.WeightedGrade(
                    BigDecimal.valueOf(90), BigDecimal.valueOf(100), BigDecimal.valueOf(60))
            );

            var result = calculator.calculate(grades);

            assertThat(result.average()).isEqualByComparingTo("88.00");
        }

        @Test
        @DisplayName("should return 100% when score equals maxScore")
        void should_returnFullPercentage_when_scoreEqualsMaxScore() {
            var grades = List.of(new AverageCalculator.WeightedGrade(
                BigDecimal.valueOf(100), BigDecimal.valueOf(100), BigDecimal.valueOf(100)));

            var result = calculator.calculate(grades);

            assertThat(result.average()).isEqualByComparingTo("100.00");
        }

        @Test
        @DisplayName("should return 0% when score is zero")
        void should_returnZero_when_scoreIsZero() {
            var grades = List.of(new AverageCalculator.WeightedGrade(
                BigDecimal.ZERO, BigDecimal.valueOf(100), BigDecimal.valueOf(100)));

            var result = calculator.calculate(grades);

            assertThat(result.average()).isEqualByComparingTo("0.00");
        }

        @Test
        @DisplayName("should round average to 2 decimal places")
        void should_roundToTwoDecimals_when_irrationalResult() {
            var grades = List.of(new AverageCalculator.WeightedGrade(
                BigDecimal.valueOf(2), BigDecimal.valueOf(3), BigDecimal.valueOf(100)));

            var result = calculator.calculate(grades);

            assertThat(result.average()).isEqualByComparingTo("66.67");
        }

        @Test
        @DisplayName("should handle very large and very small scores")
        void should_handleExtremeValues_when_largeAndSmallScores() {
            var grades = List.of(
                new AverageCalculator.WeightedGrade(
                    BigDecimal.valueOf(0.001), BigDecimal.valueOf(100), BigDecimal.valueOf(50)),
                new AverageCalculator.WeightedGrade(
                    BigDecimal.valueOf(999999.99), BigDecimal.valueOf(1000000), BigDecimal.valueOf(50))
            );

            var result = calculator.calculate(grades);

            assertThat(result.average()).isNotNull();
            assertThat(result.gradedEvaluations()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("isAtRisk")
    class IsAtRisk {

        private final AverageCalculator.AverageResult result = new AverageCalculator.AverageResult(
            BigDecimal.valueOf(70.00), 1, 1);

        @Test
        @DisplayName("should return true when average is below threshold")
        void should_returnTrue_when_belowThreshold() {
            assertThat(result.isAtRisk(BigDecimal.valueOf(75))).isTrue();
        }

        @Test
        @DisplayName("should return false when average is above threshold")
        void should_returnFalse_when_aboveThreshold() {
            assertThat(result.isAtRisk(BigDecimal.valueOf(65))).isFalse();
        }

        @Test
        @DisplayName("should return false when average equals threshold")
        void should_returnFalse_when_equalToThreshold() {
            assertThat(result.isAtRisk(BigDecimal.valueOf(70))).isFalse();
        }
    }
}
