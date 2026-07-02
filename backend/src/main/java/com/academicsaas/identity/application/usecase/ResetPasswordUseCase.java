package com.academicsaas.identity.application.usecase;

import com.academicsaas.identity.application.port.PasswordHasher;
import com.academicsaas.identity.domain.model.valueobject.UserId;
import com.academicsaas.identity.domain.repository.PasswordResetTokenRepository;
import com.academicsaas.identity.domain.repository.UserRepository;
import com.academicsaas.shared.exception.ValidationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ResetPasswordUseCase {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public ResetPasswordUseCase(PasswordResetTokenRepository tokenRepository,
                                 UserRepository userRepository,
                                 PasswordHasher passwordHasher) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    public record Request(String token, String newPassword) {}
    public record Response(boolean success) {}

    public Response execute(Request request) {
        var tokenOpt = tokenRepository.findByToken(request.token());

        if (tokenOpt.isEmpty()) {
            throw new ValidationException("Token de recuperación inválido o expirado");
        }

        var resetToken = tokenOpt.get();

        if (!resetToken.isValid()) {
            throw new ValidationException("Token de recuperación inválido o expirado");
        }

        if (request.newPassword() == null || request.newPassword().length() < 8) {
            throw new ValidationException("La contraseña debe tener al menos 8 caracteres");
        }

        var userOpt = userRepository.findById(new UserId(resetToken.getUserId()));
        if (userOpt.isEmpty()) {
            throw new ValidationException("Usuario no encontrado");
        }

        var user = userOpt.get();
        var newHash = passwordHasher.hash(request.newPassword());
        user.changePassword(newHash);
        userRepository.save(user);

        resetToken.markAsUsed();
        tokenRepository.save(resetToken);

        return new Response(true);
    }
}
