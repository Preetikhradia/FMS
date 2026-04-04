package com.FMS.dashboard.port.in;

import com.FMS.dashboard.dto.user.CreateUserRequest;
import com.FMS.dashboard.dto.user.UserResponse;
import com.FMS.dashboard.model.Role;

import java.util.List;

public interface UserUseCase {
    List<UserResponse> listAllUsers();
    UserResponse createUser(CreateUserRequest request);
    UserResponse updateRole(Long userId, Role newRole);
    void toggleStatus(Long userId);
}