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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthServiceImpl implements IAuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public AuthResponse register(RegisterUserDto dto) {
        log.info("Registering new user: {}", dto.getUsername());

//        validar si el username ya existe
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            log.warn("Username already exists: {}", dto.getUsername());
            throw new DuplicateUsernameException("Username already exists: " + dto.getUsername());
        }
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            log.warn("Email already exists: {}", dto.getEmail());
            throw new DuplicateEmailException("Email '" + dto.getEmail() + "' already registered");
        }

//        obtener o crear el rol ROLE_USER
        Role roleUser = roleRepository.findByName("ROLE_USER").orElseGet(() -> {
            Role newRole = new Role();
            newRole.setName("ROLE_USER");
            return roleRepository.save(newRole);
        });

//        crear el usuario y guardarlo en la base de datos, pasamos
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(roleUser);

        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", savedUser.getUsername());

//        generar tokens JWT para el usuario registrado
        String accessToken = jwtService.generateAccessToken(savedUser);
        String refreshToken = jwtService.generateRefreshToken(savedUser);
//retotnamos un objeto AuthResponse con un mensaje personalizado y los tokens generados
        return new AuthResponse("User registered successfully", accessToken, refreshToken);
    }

    @Override
    public AuthResponse login(LoginUserDto dto) {

        User user = userRepository.findByUsername(dto.getUsername()).orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return new AuthResponse("Login successful", jwtService.generateAccessToken(user), jwtService.generateRefreshToken(user));
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {

        String username = jwtService.extractUsername(refreshToken);
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new RuntimeException("Refresh token invalid");
        }

        return new AuthResponse("Token refreshed successfully", jwtService.generateAccessToken(user), jwtService.generateRefreshToken(user));
    }
}