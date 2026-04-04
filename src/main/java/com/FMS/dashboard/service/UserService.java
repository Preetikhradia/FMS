package com.FMS.dashboard.service;

import com.FMS.dashboard.dto.user.CreateUserRequest;
import com.FMS.dashboard.dto.user.UserResponse;
import com.FMS.dashboard.exception.AppException;
import com.FMS.dashboard.model.Role;
import com.FMS.dashboard.model.User;
import com.FMS.dashboard.port.in.UserUseCase;
import com.FMS.dashboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserUseCase {

    private final UserRepository  userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> listAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw AppException.conflict("Email already in use");
        }
        User user = User.builder()
                .email(request.getEmail().toLowerCase().trim())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .role(request.getRole())    // admin can assign any role
                .active(true)
                .build();
        return toResponse(userRepository.save(user));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UserResponse updateRole(Long userId, Role newRole) {
        User user = findOrThrow(userId);
        user.setRole(newRole);
        log.info("Role updated: userId={} newRole={}", userId, newRole);
        return toResponse(userRepository.save(user));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void toggleStatus(Long userId) {
        User user = findOrThrow(userId);
        user.setActive(!user.isActive());
        log.info("User status toggled: userId={} active={}", userId, user.isActive());
        userRepository.save(user);
    }

    private User findOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("User not found with id: " + id));
    }

    private UserResponse toResponse(User u) {
        return UserResponse.builder()
                .id(u.getId()).email(u.getEmail())
                .name(u.getName()).role(u.getRole())
                .active(u.isActive()).createdAt(u.getCreatedAt())
                .build();
    }
}