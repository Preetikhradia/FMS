package com.FMS.dashboard.Adapter;

import com.FMS.dashboard.dto.user.CreateUserRequest;
import com.FMS.dashboard.dto.user.UserResponse;
import com.FMS.dashboard.model.Role;
import com.FMS.dashboard.port.in.UserUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Inbound adapter: bridges UserController → UserUseCase port.
 *
 * Any request-level transformation (e.g. email normalisation) lives here,
 * not inside the service.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserCommandAdapter {

    private final UserUseCase userUseCase;

    public List<UserResponse> listUsers() {
        log.debug("Adapter → listUsers");
        return userUseCase.listAllUsers();
    }

    public UserResponse createUser(CreateUserRequest request) {
        log.info("Adapter → createUser | email={} role={}", request.getEmail(), request.getRole());
        // Normalise email to lowercase before it reaches the domain
        request.setEmail(request.getEmail().toLowerCase().trim());
        return userUseCase.createUser(request);
    }

    public UserResponse updateRole(Long userId, Role newRole) {
        log.info("Adapter → updateRole | userId={} newRole={}", userId, newRole);
        return userUseCase.updateRole(userId, newRole);
    }

    public void toggleStatus(Long userId) {
        log.info("Adapter → toggleStatus | userId={}", userId);
        userUseCase.toggleStatus(userId);
    }
}