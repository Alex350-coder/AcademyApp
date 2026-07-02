package com.academicsaas.identity.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.academicsaas.identity.domain.model.valueobject.Email;
import com.academicsaas.identity.domain.model.valueobject.InstitutionId;
import com.academicsaas.identity.domain.model.valueobject.UserId;
import com.academicsaas.identity.domain.model.valueobject.UserStatus;
import com.academicsaas.shared.exception.ValidationException;
import java.time.Instant;
import java.util.HashSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("User")
class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = User.create(
            UserId.generate(),
            new Email("test@example.com"),
            "hashedPassword123",
            "John",
            "Doe",
            InstitutionId.generate()
        );
    }

    @Nested
    @DisplayName("construction")
    class Construction {

        @Test
        @DisplayName("should create user with valid data")
        void should_createUser_when_validData() {
            assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
            assertThat(user.getEmail().value()).isEqualTo("test@example.com");
            assertThat(user.getFullName()).isEqualTo("John Doe");
        }
    }

    @Nested
    @DisplayName("deactivate")
    class Deactivate {

        @Test
        @DisplayName("should set status to INACTIVE when deactivated")
        void should_setInactive_when_deactivated() {
            user.deactivate();

            assertThat(user.getStatus()).isEqualTo(UserStatus.INACTIVE);
        }

        @Test
        @DisplayName("should throw when deactivating already inactive user")
        void should_throw_when_alreadyInactive() {
            user.deactivate();

            assertThatThrownBy(() -> user.deactivate())
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("already inactive");
        }
    }

    @Nested
    @DisplayName("activate")
    class Activate {

        @Test
        @DisplayName("should set status to ACTIVE when reactivated")
        void should_setActive_when_reactivated() {
            user.deactivate();
            user.activate();

            assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
        }
    }

    @Nested
    @DisplayName("equals")
    class Equals {

        @Test
        @DisplayName("should use reference equality since equals is not overridden")
        void should_notBeEqual_when_differentInstances() {
            var sameDataUser = new User(
                user.getId(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                user.getStatus(),
                user.getInstitutionId(),
                user.getRoles(),
                user.getLastLoginAt(),
                user.getCreatedAt(),
                user.getUpdatedAt()
            );

            assertThat(user).isNotEqualTo(sameDataUser);
        }

        @Test
        @DisplayName("should be equal when same instance")
        void should_beEqual_when_sameInstance() {
            assertThat(user).isEqualTo(user);
        }
    }
}
