package com.FMS.dashboard.Controller;

import com.FMS.dashboard.Adapter.UserCommandAdapter;
import com.FMS.dashboard.dto.user.CreateUserRequest;
import com.FMS.dashboard.dto.user.UserResponse;
import com.FMS.dashboard.model.Role;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserCommandAdapter userAdapter;

    @GetMapping
    public ResponseEntity<List<UserResponse>> listUsers() {
        return ResponseEntity.ok(userAdapter.listUsers());
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userAdapter.createUser(request));
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<UserResponse> updateRole(
            @PathVariable Long id,
            @RequestParam Role role) {
        return ResponseEntity.ok(userAdapter.updateRole(id, role));
    }

    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<Void> toggleStatus(@PathVariable Long id) {
        userAdapter.toggleStatus(id);
        return ResponseEntity.noContent().build();
    }
}