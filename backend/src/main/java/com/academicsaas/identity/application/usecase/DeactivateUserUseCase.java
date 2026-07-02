package com.academicsaas.identity.application.usecase;

import com.academicsaas.identity.domain.model.valueobject.UserId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.academicsaas.identity.domain.repository.UserRepository;
import com.academicsaas.shared.exception.NotFoundException;
import com.academicsaas.shared.exception.ValidationException;

@Service
@Transactional
public class DeactivateUserUseCase {

    private final UserRepository userRepository;

    public DeactivateUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public record Request(String userId) {}

    public void execute(Request request) {
        var userId = UserId.fromString(request.userId());
        var user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User", request.userId()));

        if (user.hasRole("DIRECTOR")) {
            var remainingDirectors = userRepository.findAll().stream()
                .filter(u -> u.hasRole("DIRECTOR") && !u.getId().equals(userId))
                .count();
            if (remainingDirectors == 0) {
                throw new ValidationException("Cannot deactivate the only DIRECTOR in the institution");
            }
        }

        user.deactivate();
        userRepository.save(user);
    }
}
