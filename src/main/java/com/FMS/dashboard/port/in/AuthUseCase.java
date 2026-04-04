package com.FMS.dashboard.port.in;

import com.FMS.dashboard.dto.auth.AuthResponse;
import com.FMS.dashboard.dto.auth.LoginRequest;
import com.FMS.dashboard.dto.user.CreateUserRequest;
import com.FMS.dashboard.dto.user.UserResponse;

public interface AuthUseCase {
    AuthResponse  login(LoginRequest request);
    UserResponse  register(CreateUserRequest request);
}