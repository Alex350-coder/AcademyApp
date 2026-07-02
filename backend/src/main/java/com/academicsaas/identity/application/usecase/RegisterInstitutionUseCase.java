package com.academicsaas.identity.application.usecase;

import com.academicsaas.identity.application.port.EventPublisher;
import com.academicsaas.identity.application.port.PasswordHasher;
import com.academicsaas.identity.domain.event.UserRegisteredEvent;
import com.academicsaas.identity.domain.exception.DuplicateEmailException;
import com.academicsaas.identity.domain.model.Institution;
import com.academicsaas.identity.domain.model.Role;
import com.academicsaas.identity.domain.model.User;
import com.academicsaas.identity.domain.model.valueobject.Email;
import com.academicsaas.identity.domain.model.valueobject.InstitutionId;
import com.academicsaas.identity.domain.model.valueobject.UserId;
import com.academicsaas.identity.domain.repository.InstitutionRepository;
import com.academicsaas.identity.domain.repository.RoleRepository;
import com.academicsaas.identity.domain.repository.UserRepository;
import com.academicsaas.shared.exception.ValidationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RegisterInstitutionUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final InstitutionRepository institutionRepository;
    private final PasswordHasher passwordHasher;
    private final EventPublisher eventPublisher;

    public RegisterInstitutionUseCase(
        UserRepository userRepository,
        RoleRepository roleRepository,
        InstitutionRepository institutionRepository,
        PasswordHasher passwordHasher,
        EventPublisher eventPublisher
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.institutionRepository = institutionRepository;
        this.passwordHasher = passwordHasher;
        this.eventPublisher = eventPublisher;
    }

    public record Request(
        String email,
        String password,
        String firstName,
        String lastName,
        String institutionName,
        String institutionCode,
        String institutionAddress,
        String institutionPhone
    ) {}

    public record Response(
        String userId,
        String email,
        String fullName,
        String institutionId,
        String institutionName,
        String institutionCode
    ) {}

    public Response execute(Request request) {
        var email = new Email(request.email());

        if (userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException(request.email());
        }

        if (institutionRepository.existsByCode(request.institutionCode().toUpperCase())) {
            throw new ValidationException("El código de institución '%s' ya está en uso".formatted(request.institutionCode()));
        }

        validatePassword(request.password());

        var institutionId = InstitutionId.generate();
        var institution = Institution.create(
            institutionId,
            request.institutionName().trim(),
            request.institutionCode().toUpperCase().trim(),
            request.institutionAddress(),
            request.institutionPhone(),
            email.value()
        );
        institutionRepository.save(institution);

        var userId = UserId.generate();
        var passwordHash = passwordHasher.hash(request.password());

        var user = User.create(
            userId,
            email,
            passwordHash,
            request.firstName().trim(),
            request.lastName().trim(),
            institutionId
        );

        var role = roleRepository.findByName("DIRECTOR")
            .orElseThrow(() -> new ValidationException("Role DIRECTOR not found"));
        user.assignRole(role);

        var saved = userRepository.save(user);

        eventPublisher.publish(UserRegisteredEvent.from(saved));

        return new Response(
            saved.getId().value().toString(),
            saved.getEmail().value(),
            saved.getFullName(),
            institutionId.value().toString(),
            institution.getName(),
            institution.getCode()
        );
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new ValidationException("La contraseña debe tener al menos 8 caracteres");
        }
        if (!password.matches(".*\\d.*")) {
            throw new ValidationException("La contraseña debe contener al menos un número");
        }
        if (!password.matches(".*[a-zA-Z].*")) {
            throw new ValidationException("La contraseña debe contener al menos una letra");
        }
    }
}
