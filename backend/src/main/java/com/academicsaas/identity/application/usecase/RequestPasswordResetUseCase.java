package com.academicsaas.identity.application.usecase;

import com.academicsaas.identity.domain.model.PasswordResetToken;
import com.academicsaas.identity.domain.model.valueobject.Email;
import com.academicsaas.identity.domain.repository.PasswordResetTokenRepository;
import com.academicsaas.identity.domain.repository.UserRepository;
import java.time.Instant;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RequestPasswordResetUseCase {

    private static final Logger log = LoggerFactory.getLogger(RequestPasswordResetUseCase.class);
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;

    public RequestPasswordResetUseCase(UserRepository userRepository, PasswordResetTokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    public record Request(String email) {}
    public record Response(String resetToken) {}

    public Response execute(Request request) {
        var email = new Email(request.email());
        var userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            log.warn("Password reset requested for non-existent email: {}", request.email());
            return new Response("");
        }

        var user = userOpt.get();

        tokenRepository.deleteByUserId(user.getId().value());

        var token = UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString();
        var expiresAt = Instant.now().plusSeconds(3600);
        var resetToken = PasswordResetToken.create(user.getId().value(), token, expiresAt);
        tokenRepository.save(resetToken);

        log.info("Password reset token created for user: {}", user.getEmail().value());

        return new Response(token);
    }
}
