package com.FMS.dashboard.Controller;


import com.FMS.dashboard.dto.UserDTO;
import com.FMS.dashboard.model.User;
import com.FMS.dashboard.service.UserService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User createUser(@Valid @RequestBody UserDTO dto) {
        return userService.createUser(dto);
    }

    @GetMapping
    public List<User> getUsers() {
        return userService.getAllUsers();
    }
}