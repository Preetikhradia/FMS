package com.FMS.dashboard.service;

import com.FMS.dashboard.dto.auth.AuthResponse;
import com.FMS.dashboard.dto.auth.LoginRequest;
import com.FMS.dashboard.dto.user.CreateUserRequest;
import com.FMS.dashboard.dto.user.UserResponse;
import com.FMS.dashboard.exception.AppException;
import com.FMS.dashboard.model.Role;
import com.FMS.dashboard.model.User;
import com.FMS.dashboard.port.in.AuthUseCase;
import com.FMS.dashboard.repository.UserRepository;
import com.FMS.dashboard.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService implements AuthUseCase {

    private final AuthenticationManager authenticationManager;
    private final UserRepository        userRepository;
    private final PasswordEncoder       passwordEncoder;
    private final JwtService            jwtService;

    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(), request.getPassword()));
        } catch (BadCredentialsException ex) {
            throw AppException.unauthorized("Invalid email or password");
        }

        User user = userRepository.findByEmailAndActiveTrue(request.getEmail())
                .orElseThrow(() -> AppException.forbidden("Account is deactivated"));

        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());
        log.info("User logged in: {} role={}", user.getEmail(), user.getRole());

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .build();
    }

    @Override
    @Transactional
    public UserResponse register(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw AppException.conflict("Email is already registered");
        }
        User user = User.builder()
                .email(request.getEmail().toLowerCase().trim())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .role(Role.VIEWER)
                .active(true)
                .build();

        User saved = userRepository.save(user);
        log.info("New user registered: {}", saved.getEmail());
        return toResponse(saved);
    }

    private UserResponse toResponse(User u) {
        return UserResponse.builder()
                .id(u.getId()).email(u.getEmail())
                .name(u.getName()).role(u.getRole())
                .active(u.isActive()).createdAt(u.getCreatedAt())
                .build();
    }
}