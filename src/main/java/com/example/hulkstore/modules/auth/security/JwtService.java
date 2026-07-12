package com.example.hulkstore.modules.auth.security;

import com.example.hulkstore.modules.auth.model.User;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {

    String generateAccessToken(User user);

    String generateRefreshToken(User user);

    String extractUsername(String token);

    boolean isTokenValid(String token, User user);

    boolean isTokenValid(String token, UserDetails userDetails);
}

