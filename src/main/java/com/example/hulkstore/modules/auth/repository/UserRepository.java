package com.example.hulkstore.modules.auth.repository;

import com.example.hulkstore.modules.auth.model.User;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    boolean existsByUsername(@NotBlank(message = "Username is required") String username);
}
