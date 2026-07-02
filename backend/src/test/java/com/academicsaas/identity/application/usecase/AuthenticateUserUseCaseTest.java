package com.academicsaas.identity.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.academicsaas.identity.application.port.PasswordHasher;
import com.academicsaas.identity.domain.exception.InvalidCredentialsException;
import com.academicsaas.identity.domain.model.Role;
import com.academicsaas.identity.domain.model.User;
import com.academicsaas.identity.domain.model.valueobject.Email;
import com.academicsaas.identity.domain.model.valueobject.UserId;
import com.academicsaas.identity.domain.model.valueobject.UserStatus;
import com.academicsaas.identity.domain.repository.UserRepository;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("AuthenticateUserUseCase")
class AuthenticateUserUseCaseTest {

    private UserRepository userRepository;
    private PasswordHasher passwordHasher;
    private AuthenticateUserUseCase useCase;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordHasher = mock(PasswordHasher.class);
        useCase = new AuthenticateUserUseCase(userRepository, passwordHasher);
    }

    private User createActiveUser() {
        return new User(
            UserId.generate(),
            new Email("user@example.com"),
            "hashedPassword",
            "John",
            "Doe",
            null,
            UserStatus.ACTIVE,
            Set.of(new Role(UUID.randomUUID(), "STUDENT", "Student role", Set.of())),
            null,
            java.time.Instant.now(),
            java.time.Instant.now()
        );
    }

    @Nested
    @DisplayName("execute")
    class Execute {

        @Test
        @DisplayName("should authenticate successfully with valid credentials")
        void should_authenticate_when_validCredentials() {
            var request = new AuthenticateUserUseCase.Request("user@example.com", "correctPassword");
            var user = createActiveUser();

            when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
            when(passwordHasher.matches(request.password(), user.getPasswordHash())).thenReturn(true);
            when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

            var response = useCase.execute(request);

            assertThat(response.email()).isEqualTo("user@example.com");
            assertThat(response.fullName()).isEqualTo("John Doe");
            verify(userRepository).save(any());
        }

        @Test
        @DisplayName("should throw InvalidCredentialsException for wrong password")
        void should_throw_when_wrongPassword() {
            var request = new AuthenticateUserUseCase.Request("user@example.com", "wrongPassword");
            var user = createActiveUser();

            when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
            when(passwordHasher.matches(request.password(), user.getPasswordHash())).thenReturn(false);

            assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(InvalidCredentialsException.class);
        }

        @Test
        @DisplayName("should throw InvalidCredentialsException for inactive user")
        void should_throw_when_inactiveUser() {
            var request = new AuthenticateUserUseCase.Request("user@example.com", "correctPassword");
            var user = new User(
                UserId.generate(),
                new Email("user@example.com"),
                "hashedPassword",
                "John",
                "Doe",
                null,
                UserStatus.INACTIVE,
                Set.of(),
                null,
                java.time.Instant.now(),
                java.time.Instant.now()
            );

            when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
            when(passwordHasher.matches(request.password(), user.getPasswordHash())).thenReturn(true);

            assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(InvalidCredentialsException.class);
        }

        @Test
        @DisplayName("should throw InvalidCredentialsException for non-existent email")
        void should_throw_when_emailNotFound() {
            var request = new AuthenticateUserUseCase.Request("unknown@example.com", "password");

            when(userRepository.findByEmail(any())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(InvalidCredentialsException.class);
        }
    }
}
