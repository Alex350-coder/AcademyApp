package com.academicsaas.identity.application.usecase;

import com.academicsaas.identity.application.port.PasswordHasher;
import com.academicsaas.identity.domain.exception.InvalidCredentialsException;
import com.academicsaas.identity.domain.model.User;
import com.academicsaas.identity.domain.model.valueobject.Email;
import com.academicsaas.identity.domain.repository.InstitutionRepository;
import com.academicsaas.identity.domain.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthenticateUserUseCase {

    private final UserRepository userRepository;
    private final InstitutionRepository institutionRepository;
    private final PasswordHasher passwordHasher;

    public AuthenticateUserUseCase(
        UserRepository userRepository,
        InstitutionRepository institutionRepository,
        PasswordHasher passwordHasher
    ) {
        this.userRepository = userRepository;
        this.institutionRepository = institutionRepository;
        this.passwordHasher = passwordHasher;
    }

    public record Request(String email, String password, String institutionCode) {}

    public record Response(
        String userId,
        String email,
        String fullName,
        List<String> roles,
        UserStatus status,
        String institutionId,
        String institutionName,
        String institutionCode
    ) {
        public record UserStatus(String code, String name) {}
    }

    public Response execute(Request request) {
        var email = new Email(request.email());
        var user = userRepository.findByEmail(email)
            .orElseThrow(InvalidCredentialsException::new);

        if (!passwordHasher.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        if (!user.getStatus().canAuthenticate()) {
            throw new InvalidCredentialsException();
        }

        var institution = institutionRepository.findById(user.getInstitutionId())
            .orElseThrow(InvalidCredentialsException::new);

        if (!institution.isActive()) {
            throw new InvalidCredentialsException();
        }

        if (request.institutionCode() != null && !request.institutionCode().isBlank()) {
            if (!institution.getCode().equals(request.institutionCode().toUpperCase().trim())) {
                throw new InvalidCredentialsException();
            }
        }

        user.recordLogin();
        userRepository.save(user);

        var roles = user.getRoles().stream()
            .map(r -> r.getName())
            .toList();

        return new Response(
            user.getId().value().toString(),
            user.getEmail().value(),
            user.getFullName(),
            roles,
            new Response.UserStatus(
                user.getStatus().name(),
                user.getStatus().name()
            ),
            institution.getId().value().toString(),
            institution.getName(),
            institution.getCode()
        );
    }
}
