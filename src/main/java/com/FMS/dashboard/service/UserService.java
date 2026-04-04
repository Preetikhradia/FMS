package com.FMS.dashboard.service;

import com.FMS.dashboard.dto.UserDTO;
import com.FMS.dashboard.model.User;
import com.FMS.dashboard.port.in.UserUseCase;
import com.FMS.dashboard.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserUseCase {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // CREATE USER
    public User createUser(UserDTO dto) {

        // Optional: check duplicate email
        userRepository.findByEmail(dto.getEmail()).ifPresent(u -> {
            throw new RuntimeException("Email already exists");
        });

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setRole(dto.getRole());
        user.setActive(true); // default active

        return userRepository.save(user);
    }

    // GET ALL USERS
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // GET USER BY ID
    public User getUserById(Long id) {
        return userRepository.findById(id)   // ✅ FIXED
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // DELETE USER
    public void deleteUser(Long id) {
        userRepository.deleteById(id);  // ✅ FIXED
    }
}