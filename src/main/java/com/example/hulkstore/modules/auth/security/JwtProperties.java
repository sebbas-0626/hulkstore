package com.example.hulkstore.modules.auth.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.*;

@Getter
@Setter
@ConfigurationProperties(prefix = "jwt")

//propiedades de jwt
public class JwtProperties {
    private String secret;
    private Long expirationMs;
    private Long refreshExpirationMs;
}