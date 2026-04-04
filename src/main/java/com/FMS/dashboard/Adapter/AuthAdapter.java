package com.FMS.dashboard.Adapter;

import com.FMS.dashboard.dto.auth.AuthResponse;
import com.FMS.dashboard.dto.auth.LoginRequest;
import com.FMS.dashboard.dto.user.CreateUserRequest;
import com.FMS.dashboard.dto.user.UserResponse;
import com.FMS.dashboard.port.in.AuthUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthAdapter {

    private final AuthUseCase authUseCase;

    public AuthResponse login(LoginRequest request) {
        log.debug("Adapter → login | email={}", request.getEmail());
        request.setEmail(request.getEmail().toLowerCase().trim());
        return authUseCase.login(request);
    }

    public UserResponse register(CreateUserRequest request) {
        log.debug("Adapter → register | email={}", request.getEmail());
        request.setEmail(request.getEmail().toLowerCase().trim());
        return authUseCase.register(request);
    }
}