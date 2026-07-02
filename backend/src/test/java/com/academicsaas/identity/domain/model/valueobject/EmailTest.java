package com.academicsaas.identity.domain.model.valueobject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.academicsaas.shared.exception.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Email")
class EmailTest {

    @Nested
    @DisplayName("construction")
    class Construction {

        @Test
        @DisplayName("should create valid email with standard format")
        void should_createEmail_when_standardFormat() {
            var email = new Email("user@example.com");

            assertThat(email.value()).isEqualTo("user@example.com");
        }

        @Test
        @DisplayName("should create valid email with subdomain")
        void should_createEmail_when_subdomain() {
            var email = new Email("user@sub.example.com");

            assertThat(email.value()).isEqualTo("user@sub.example.com");
        }

        @Test
        @DisplayName("should validate normalized lowercase version but store original")
        void should_storeOriginalCase_when_mixedCaseProvided() {
            var email = new Email("User@Example.Com");

            assertThat(email.value()).isEqualTo("User@Example.Com");
        }

        @Test
        @DisplayName("should throw for null value")
        void should_throw_when_null() {
            assertThatThrownBy(() -> new Email(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Email must not be null");
        }

        @Test
        @DisplayName("should throw for blank value")
        void should_throw_when_blank() {
            assertThatThrownBy(() -> new Email("  "))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("blank");
        }

        @Test
        @DisplayName("should throw for invalid format without @")
        void should_throw_when_noAtSign() {
            assertThatThrownBy(() -> new Email("invalid-email"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("invalid format");
        }

        @Test
        @DisplayName("should throw for invalid format without domain")
        void should_throw_when_noDomain() {
            assertThatThrownBy(() -> new Email("user@"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("invalid format");
        }
    }
}
