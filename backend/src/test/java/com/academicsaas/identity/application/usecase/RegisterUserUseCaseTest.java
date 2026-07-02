package com.academicsaas.identity.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.academicsaas.identity.application.port.EventPublisher;
import com.academicsaas.identity.application.port.PasswordHasher;
import com.academicsaas.identity.domain.event.UserRegisteredEvent;
import com.academicsaas.identity.domain.exception.DuplicateEmailException;
import com.academicsaas.identity.domain.model.Role;
import com.academicsaas.identity.domain.model.User;
import com.academicsaas.identity.domain.model.valueobject.Email;
import com.academicsaas.identity.domain.repository.RoleRepository;
import com.academicsaas.identity.domain.repository.UserRepository;
import com.academicsaas.shared.exception.ValidationException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("RegisterUserUseCase")
class RegisterUserUseCaseTest {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordHasher passwordHasher;
    private EventPublisher eventPublisher;
    private RegisterUserUseCase useCase;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        roleRepository = mock(RoleRepository.class);
        passwordHasher = mock(PasswordHasher.class);
        eventPublisher = mock(EventPublisher.class);
        useCase = new RegisterUserUseCase(userRepository, roleRepository, passwordHasher, eventPublisher);
    }

    @Nested
    @DisplayName("execute")
    class Execute {

        @Test
        @DisplayName("should save user and publish event when data is valid")
        void should_registerUser_when_validData() {
            var request = new RegisterUserUseCase.Request(
                "user@example.com", "Password1", "Jane", "Doe", "STUDENT");
            var role = new Role(UUID.randomUUID(), "STUDENT", "Student role", Set.of());

            when(userRepository.existsByEmail(any())).thenReturn(false);
            when(passwordHasher.hash(request.password())).thenReturn("hashedPassword");
            when(roleRepository.findByName("STUDENT")).thenReturn(Optional.of(role));
            when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

            var response = useCase.execute(request);

            assertThat(response.email()).isEqualTo("user@example.com");
            assertThat(response.fullName()).isEqualTo("Jane Doe");
            verify(userRepository).save(any());
            verify(eventPublisher).publish(any(UserRegisteredEvent.class));
        }

        @Test
        @DisplayName("should throw DuplicateEmailException when email already exists")
        void should_throwDuplicateEmail_when_emailExists() {
            var request = new RegisterUserUseCase.Request(
                "existing@example.com", "Password1", "Jane", "Doe", null);

            when(userRepository.existsByEmail(any())).thenReturn(true);

            assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("already registered");

            verify(userRepository, never()).save(any());
            verify(eventPublisher, never()).publish(any());
        }

        @Test
        @DisplayName("should throw ValidationException when password is too short")
        void should_throwValidation_when_weakPassword() {
            var request = new RegisterUserUseCase.Request(
                "user@example.com", "Short1", "Jane", "Doe", null);

            when(userRepository.existsByEmail(any())).thenReturn(false);

            assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("at least 8 characters");

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("should publish UserRegisteredEvent on success")
        void should_publishEvent_when_registrationSucceeds() {
            var request = new RegisterUserUseCase.Request(
                "event@test.com", "Password1", "Event", "Test", null);

            when(userRepository.existsByEmail(any())).thenReturn(false);
            when(passwordHasher.hash(any())).thenReturn("hashed");
            when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

            useCase.execute(request);

            verify(eventPublisher).publish(any(UserRegisteredEvent.class));
        }
    }
}
