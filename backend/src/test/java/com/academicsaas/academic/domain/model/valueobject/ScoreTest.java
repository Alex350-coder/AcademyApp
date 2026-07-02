package com.academicsaas.academic.domain.model.valueobject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.academicsaas.shared.exception.ValidationException;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Score")
class ScoreTest {

    @Nested
    @DisplayName("construction")
    class Construction {

        @Test
        @DisplayName("should create valid Score with positive values")
        void should_createScore_when_positiveValues() {
            var score = new Score(BigDecimal.valueOf(75), BigDecimal.valueOf(100));

            assertThat(score.value()).isEqualByComparingTo("75");
            assertThat(score.maxScore()).isEqualByComparingTo("100");
        }

        @Test
        @DisplayName("should create valid Score with zero value")
        void should_createScore_when_zeroValue() {
            var score = new Score(BigDecimal.ZERO, BigDecimal.valueOf(100));

            assertThat(score.value()).isEqualByComparingTo("0");
        }

        @Test
        @DisplayName("should throw exception when value is negative")
        void should_throw_when_valueNegative() {
            assertThatThrownBy(() -> new Score(BigDecimal.valueOf(-1), BigDecimal.valueOf(100)))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("negative");
        }

        @Test
        @DisplayName("should throw exception when value exceeds maxScore")
        void should_throw_when_valueExceedsMaxScore() {
            assertThatThrownBy(() -> new Score(BigDecimal.valueOf(101), BigDecimal.valueOf(100)))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("exceed");
        }

        @Test
        @DisplayName("should throw exception when maxScore is zero")
        void should_throw_when_maxScoreZero() {
            assertThatThrownBy(() -> new Score(BigDecimal.valueOf(50), BigDecimal.ZERO))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("greater than zero");
        }

        @Test
        @DisplayName("should throw exception when maxScore is negative")
        void should_throw_when_maxScoreNegative() {
            assertThatThrownBy(() -> new Score(BigDecimal.valueOf(50), BigDecimal.valueOf(-10)))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("greater than zero");
        }
    }

    @Nested
    @DisplayName("percentage")
    class Percentage {

        @Test
        @DisplayName("should return 100 when value equals maxScore")
        void should_return100_when_valueEqualsMaxScore() {
            var score = new Score(BigDecimal.valueOf(100), BigDecimal.valueOf(100));

            assertThat(score.percentage()).isEqualByComparingTo("100.0000");
        }

        @Test
        @DisplayName("should return 50 for half score")
        void should_return50_when_halfScore() {
            var score = new Score(BigDecimal.valueOf(50), BigDecimal.valueOf(100));

            assertThat(score.percentage()).isEqualByComparingTo("50.0000");
        }
    }
}
