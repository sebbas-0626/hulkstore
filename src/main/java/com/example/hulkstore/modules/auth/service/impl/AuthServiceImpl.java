package com.example.hulkstore.modules.auth.service.impl;

import com.example.hulkstore.modules.auth.dto.AuthResponse;
import com.example.hulkstore.modules.auth.dto.LoginUserDto;
import com.example.hulkstore.modules.auth.dto.RegisterUserDto;
import com.example.hulkstore.modules.auth.exception.*;
import com.example.hulkstore.modules.auth.model.Role;
import com.example.hulkstore.modules.auth.model.User;
import com.example.hulkstore.modules.auth.repository.RoleRepository;
import com.example.hulkstore.modules.auth.repository.UserRepository;
import com.example.hulkstore.modules.auth.security.JwtService;
import com.example.hulkstore.modules.auth.service.interfaces.IAuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthServiceImpl implements IAuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse register(RegisterUserDto dto) {
        log.info("Registering new user: {}", dto.getUsername());

        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            log.warn("Username already exists: {}", dto.getUsername());
            throw new DuplicateUsernameException("Username already exists: " + dto.getUsername());
        }
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            log.warn("Email already exists: {}", dto.getEmail());
            throw new DuplicateEmailException("Email '" + dto.getEmail() + "' already registered");
        }

        Role roleUser = roleRepository.findByName("ROLE_USER").orElseGet(() -> {
            Role newRole = new Role();
            newRole.setName("ROLE_USER");
            return roleRepository.save(newRole);
        });

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(roleUser);

        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", savedUser.getUsername());

        return AuthResponse.builder()
                .message("User registered successfully")
                .accessToken(jwtService.generateAccessToken(savedUser))
                .refreshToken(jwtService.generateRefreshToken(savedUser))
                .userId(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .role(savedUser.getRole().getName())
                .build();
    }

    @Override
    public AuthResponse login(LoginUserDto dto) {
        log.info("Login attempt for username: {}", dto.getUsername());
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = userRepository.findByUsername(dto.getUsername())
                    .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

            if (!user.getEnabled()) {
                throw new AccountDisabledException("Account is disabled");
            }

            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);

            log.info("User logged in successfully: {}", user.getUsername());

            return AuthResponse.builder()
                    .message("Login successful")
                    .accessToken(jwtService.generateAccessToken(user))
                    .refreshToken(jwtService.generateRefreshToken(user))
                    .userId(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .role(user.getRole().getName())
                    .build();

        } catch (BadCredentialsException e) {
            log.warn("Invalid credentials for user: {}", dto.getUsername());
            throw new InvalidCredentialsException("Invalid username or password");
        }
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        String username = jwtService.extractUsername(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new InvalidTokenException("Refresh token invalid");
        }

        return AuthResponse.builder()
                .message("Token refreshed successfully")
                .accessToken(jwtService.generateAccessToken(user))
                .refreshToken(jwtService.generateRefreshToken(user))
                .build();
    }
}
