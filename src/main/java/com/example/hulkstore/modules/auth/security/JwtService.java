package com.example.hulkstore.modules.auth.security;

import com.example.hulkstore.modules.auth.model.User;

//interfaces jwt service
public interface JwtService {

    String generateAccessToken(User user);

    String generateRefreshToken(User user);

    String extractUsername(String token);

    boolean isTokenValid(String token, User user);
}

