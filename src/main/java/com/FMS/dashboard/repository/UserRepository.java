package com.FMS.dashboard.repository;

import com.FMS.dashboard.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Used by JwtFilter and AuthService
    Optional<User> findByEmailAndActiveTrue(String email);

    // Used to prevent duplicate registrations
    boolean existsByEmail(String email);
}