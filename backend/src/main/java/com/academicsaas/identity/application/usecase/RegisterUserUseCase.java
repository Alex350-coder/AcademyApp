package com.academicsaas.identity.application.usecase;

import com.academicsaas.identity.application.port.EventPublisher;
import com.academicsaas.identity.application.port.PasswordHasher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.academicsaas.identity.domain.event.UserRegisteredEvent;
import com.academicsaas.identity.domain.exception.DuplicateEmailException;
import com.academicsaas.identity.domain.model.Role;
import com.academicsaas.identity.domain.model.User;
import com.academicsaas.identity.domain.model.valueobject.Email;
import com.academicsaas.identity.domain.model.valueobject.InstitutionId;
import com.academicsaas.identity.domain.model.valueobject.UserId;
import com.academicsaas.identity.domain.repository.RoleRepository;
import com.academicsaas.identity.domain.repository.UserRepository;
import com.academicsaas.shared.exception.ValidationException;

@Service
@Transactional
public class RegisterUserUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordHasher passwordHasher;
    private final EventPublisher eventPublisher;

    public RegisterUserUseCase(
        UserRepository userRepository,
        RoleRepository roleRepository,
        PasswordHasher passwordHasher,
        EventPublisher eventPublisher
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordHasher = passwordHasher;
        this.eventPublisher = eventPublisher;
    }

    public record Request(
        String email,
        String password,
        String firstName,
        String lastName,
        String roleName,
        String institutionId
    ) {}

    public record Response(
        String userId,
        String email,
        String fullName
    ) {}

    public Response execute(Request request) {
        var email = new Email(request.email());

        if (userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException(request.email());
        }

        validatePassword(request.password());

        var userId = UserId.generate();
        var passwordHash = passwordHasher.hash(request.password());
        var institutionId = request.institutionId() != null
            ? InstitutionId.fromString(request.institutionId())
            : null;

        var user = User.create(
            userId,
            email,
            passwordHash,
            request.firstName().trim(),
            request.lastName().trim(),
            institutionId
        );

        if (request.roleName() != null && !request.roleName().isBlank()) {
            var role = roleRepository.findByName(request.roleName().toUpperCase())
                .orElseThrow(() -> new ValidationException("Role '%s' not found".formatted(request.roleName())));
            user.assignRole(role);
        }

        var saved = userRepository.save(user);

        eventPublisher.publish(UserRegisteredEvent.from(saved));

        return new Response(
            saved.getId().value().toString(),
            saved.getEmail().value(),
            saved.getFullName()
        );
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new ValidationException("Password must be at least 8 characters");
        }
        if (!password.matches(".*\\d.*")) {
            throw new ValidationException("Password must contain at least one number");
        }
        if (!password.matches(".*[a-zA-Z].*")) {
            throw new ValidationException("Password must contain at least one letter");
        }
    }
}
