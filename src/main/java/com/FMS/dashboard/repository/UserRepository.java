package com.FMS.dashboard.repository;

import com.FMS.dashboard.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAndActiveTrue(String email);
    boolean existsByEmail(String email);
    Optional<Object> findByEmailIgnoreCaseAndActiveTrue(String email);
}