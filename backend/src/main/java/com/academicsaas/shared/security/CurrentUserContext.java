package com.academicsaas.shared.security;

import com.academicsaas.identity.infrastructure.persistence.repository.SpringDataUserRepository;
import com.academicsaas.shared.exception.NotFoundException;
import java.util.UUID;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

// Resolves the calling user's own institution from the JWT-authenticated
// session, so controllers can scope reads/writes to it instead of trusting
// a client-supplied institutionId.
@Component
public class CurrentUserContext {

    private final SpringDataUserRepository userRepository;

    public CurrentUserContext(SpringDataUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UUID institutionId(Authentication auth) {
        var userId = UUID.fromString(auth.getName());
        var user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User", userId));
        var institutionId = user.getInstitutionId();
        if (institutionId == null) {
            throw new AccessDeniedException("User is not associated with an institution");
        }
        return institutionId;
    }
}
