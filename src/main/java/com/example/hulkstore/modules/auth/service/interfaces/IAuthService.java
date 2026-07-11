package com.example.hulkstore.modules.auth.service.interfaces;

import com.example.hulkstore.modules.auth.dto.AuthResponse;
import com.example.hulkstore.modules.auth.dto.LoginUserDto;
import com.example.hulkstore.modules.auth.dto.RegisterUserDto;

public interface IAuthService {
    AuthResponse register(RegisterUserDto dto);

    AuthResponse login(LoginUserDto dto);

    AuthResponse refreshToken(String refreshToken);
}
