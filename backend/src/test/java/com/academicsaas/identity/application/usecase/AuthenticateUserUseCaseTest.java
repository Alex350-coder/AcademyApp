package com.academicsaas.identity.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.academicsaas.identity.application.port.PasswordHasher;
import com.academicsaas.identity.domain.exception.InvalidCredentialsException;
import com.academicsaas.identity.domain.model.Institution;
import com.academicsaas.identity.domain.model.Role;
import com.academicsaas.identity.domain.model.User;
import com.academicsaas.identity.domain.model.valueobject.Email;
import com.academicsaas.identity.domain.model.valueobject.InstitutionId;
import com.academicsaas.identity.domain.model.valueobject.UserId;
import com.academicsaas.identity.domain.model.valueobject.UserStatus;
import com.academicsaas.identity.domain.repository.InstitutionRepository;
import com.academicsaas.identity.domain.repository.UserRepository;
import java.time.Instant;
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
    private InstitutionRepository institutionRepository;
    private PasswordHasher passwordHasher;
    private AuthenticateUserUseCase useCase;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        institutionRepository = mock(InstitutionRepository.class);
        passwordHasher = mock(PasswordHasher.class);
        useCase = new AuthenticateUserUseCase(userRepository, institutionRepository, passwordHasher);
    }

    private User createActiveUser(InstitutionId institutionId) {
        return new User(
            UserId.generate(),
            new Email("user@example.com"),
            "hashedPassword",
            "John",
            "Doe",
            null,
            UserStatus.ACTIVE,
            institutionId,
            Set.of(new Role(UUID.randomUUID(), "STUDENT", "Student role", Set.of())),
            null,
            Instant.now(),
            Instant.now()
        );
    }

    private Institution createInstitution(InstitutionId institutionId, String code, boolean active) {
        var institution = Institution.create(institutionId, "Test Institution", code, null, null, null);
        if (!active) {
            institution.deactivate();
        }
        return institution;
    }

    @Nested
    @DisplayName("execute")
    class Execute {

        @Test
        @DisplayName("should authenticate successfully with valid credentials")
        void should_authenticate_when_validCredentials() {
            var request = new AuthenticateUserUseCase.Request("user@example.com", "correctPassword", null);
            var institutionId = InstitutionId.generate();
            var user = createActiveUser(institutionId);
            var institution = createInstitution(institutionId, "TEST-001", true);

            when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
            when(passwordHasher.matches(request.password(), user.getPasswordHash())).thenReturn(true);
            when(institutionRepository.findById(institutionId)).thenReturn(Optional.of(institution));
            when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

            var response = useCase.execute(request);

            assertThat(response.email()).isEqualTo("user@example.com");
            assertThat(response.fullName()).isEqualTo("John Doe");
            assertThat(response.institutionCode()).isEqualTo("TEST-001");
            verify(userRepository).save(any());
        }

        @Test
        @DisplayName("should authenticate when institutionCode matches")
        void should_authenticate_when_institutionCodeMatches() {
            var institutionId = InstitutionId.generate();
            var request = new AuthenticateUserUseCase.Request("user@example.com", "correctPassword", "test-001");
            var user = createActiveUser(institutionId);
            var institution = createInstitution(institutionId, "TEST-001", true);

            when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
            when(passwordHasher.matches(request.password(), user.getPasswordHash())).thenReturn(true);
            when(institutionRepository.findById(institutionId)).thenReturn(Optional.of(institution));
            when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

            var response = useCase.execute(request);

            assertThat(response.institutionCode()).isEqualTo("TEST-001");
        }

        @Test
        @DisplayName("should throw InvalidCredentialsException when institutionCode does not match")
        void should_throw_when_institutionCodeMismatch() {
            var institutionId = InstitutionId.generate();
            var request = new AuthenticateUserUseCase.Request("user@example.com", "correctPassword", "OTHER-999");
            var user = createActiveUser(institutionId);
            var institution = createInstitution(institutionId, "TEST-001", true);

            when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
            when(passwordHasher.matches(request.password(), user.getPasswordHash())).thenReturn(true);
            when(institutionRepository.findById(institutionId)).thenReturn(Optional.of(institution));

            assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(InvalidCredentialsException.class);
        }

        @Test
        @DisplayName("should throw InvalidCredentialsException for wrong password")
        void should_throw_when_wrongPassword() {
            var request = new AuthenticateUserUseCase.Request("user@example.com", "wrongPassword", null);
            var user = createActiveUser(InstitutionId.generate());

            when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
            when(passwordHasher.matches(request.password(), user.getPasswordHash())).thenReturn(false);

            assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(InvalidCredentialsException.class);
        }

        @Test
        @DisplayName("should throw InvalidCredentialsException for inactive user")
        void should_throw_when_inactiveUser() {
            var request = new AuthenticateUserUseCase.Request("user@example.com", "correctPassword", null);
            var user = new User(
                UserId.generate(),
                new Email("user@example.com"),
                "hashedPassword",
                "John",
                "Doe",
                null,
                UserStatus.INACTIVE,
                InstitutionId.generate(),
                Set.of(),
                null,
                Instant.now(),
                Instant.now()
            );

            when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
            when(passwordHasher.matches(request.password(), user.getPasswordHash())).thenReturn(true);

            assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(InvalidCredentialsException.class);
        }

        @Test
        @DisplayName("should throw InvalidCredentialsException for non-existent email")
        void should_throw_when_emailNotFound() {
            var request = new AuthenticateUserUseCase.Request("unknown@example.com", "password", null);

            when(userRepository.findByEmail(any())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(InvalidCredentialsException.class);
        }

        @Test
        @DisplayName("should throw InvalidCredentialsException when institution is not found")
        void should_throw_when_institutionNotFound() {
            var institutionId = InstitutionId.generate();
            var request = new AuthenticateUserUseCase.Request("user@example.com", "correctPassword", null);
            var user = createActiveUser(institutionId);

            when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
            when(passwordHasher.matches(request.password(), user.getPasswordHash())).thenReturn(true);
            when(institutionRepository.findById(institutionId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(InvalidCredentialsException.class);
        }

        @Test
        @DisplayName("should throw InvalidCredentialsException when institution is inactive")
        void should_throw_when_institutionInactive() {
            var institutionId = InstitutionId.generate();
            var request = new AuthenticateUserUseCase.Request("user@example.com", "correctPassword", null);
            var user = createActiveUser(institutionId);
            var institution = createInstitution(institutionId, "TEST-001", false);

            when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
            when(passwordHasher.matches(request.password(), user.getPasswordHash())).thenReturn(true);
            when(institutionRepository.findById(institutionId)).thenReturn(Optional.of(institution));

            assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(InvalidCredentialsException.class);
        }
    }
}
