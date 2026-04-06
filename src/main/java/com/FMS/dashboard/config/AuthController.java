package com.FMS.dashboard.config;

import com.FMS.dashboard.Adapter.AuthAdapter;
import com.FMS.dashboard.dto.auth.AuthResponse;
import com.FMS.dashboard.dto.auth.LoginRequest;
import com.FMS.dashboard.dto.user.CreateUserRequest;
import com.FMS.dashboard.dto.user.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthAdapter authAdapter;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authAdapter.login(request));
    }
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authAdapter.register(request));
    }
}