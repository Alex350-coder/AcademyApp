package com.academicsaas.identity.application.usecase;

import com.academicsaas.identity.domain.model.Role;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.academicsaas.identity.domain.model.valueobject.UserId;
import com.academicsaas.identity.domain.repository.RoleRepository;
import com.academicsaas.identity.domain.repository.UserRepository;
import com.academicsaas.shared.exception.NotFoundException;

@Service
@Transactional
public class AssignRoleUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public AssignRoleUseCase(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public record Request(String userId, String roleName) {}

    public void execute(Request request) {
        var userId = UserId.fromString(request.userId());
        var user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User", request.userId()));

        var role = roleRepository.findByName(request.roleName().toUpperCase())
            .orElseThrow(() -> new NotFoundException("Role", request.roleName()));

        user.assignRole(role);
        userRepository.save(user);
    }
}
