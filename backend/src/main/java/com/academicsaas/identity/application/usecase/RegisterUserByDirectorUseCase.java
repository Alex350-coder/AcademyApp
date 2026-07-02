package com.academicsaas.identity.application.usecase;

import com.academicsaas.identity.application.port.EventPublisher;
import com.academicsaas.identity.application.port.PasswordHasher;
import com.academicsaas.identity.domain.event.UserRegisteredEvent;
import com.academicsaas.identity.domain.exception.DuplicateEmailException;
import com.academicsaas.identity.domain.exception.InvalidCredentialsException;
import com.academicsaas.identity.domain.model.Role;
import com.academicsaas.identity.domain.model.User;
import com.academicsaas.identity.domain.model.valueobject.Email;
import com.academicsaas.identity.domain.model.valueobject.InstitutionId;
import com.academicsaas.identity.domain.model.valueobject.UserId;
import com.academicsaas.identity.domain.repository.RoleRepository;
import com.academicsaas.identity.domain.repository.UserRepository;
import com.academicsaas.shared.exception.ValidationException;
import java.util.UUID;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RegisterUserByDirectorUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordHasher passwordHasher;
    private final EventPublisher eventPublisher;

    public RegisterUserByDirectorUseCase(
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
        String firstName,
        String lastName,
        String roleName
    ) {}

    public record Response(
        String userId,
        String email,
        String fullName,
        String temporaryPassword
    ) {}

    public Response execute(Request request) {
        var email = new Email(request.email());

        if (userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException(request.email());
        }

        var currentUserId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        var currentUser = userRepository.findById(UserId.fromString(currentUserId))
            .orElseThrow(InvalidCredentialsException::new);

        var institutionId = currentUser.getInstitutionId();

        var tempPassword = UUID.randomUUID().toString().substring(0, 12) + "A1";
        var passwordHash = passwordHasher.hash(tempPassword);

        var userId = UserId.generate();
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
            if ("DIRECTOR".equals(role.getName())) {
                throw new ValidationException("No puedes crear otro director. Contacta al superadmin.");
            }
            user.assignRole(role);
        }

        var saved = userRepository.save(user);
        eventPublisher.publish(UserRegisteredEvent.from(saved));

        return new Response(
            saved.getId().value().toString(),
            saved.getEmail().value(),
            saved.getFullName(),
            tempPassword
        );
    }
}
