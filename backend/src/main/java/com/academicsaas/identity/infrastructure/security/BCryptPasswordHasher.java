package com.academicsaas.identity.infrastructure.security;

import com.academicsaas.identity.application.port.PasswordHasher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCryptPasswordHasher implements PasswordHasher {

    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder(12);

    @Override
    public String hash(String rawPassword) {
        return ENCODER.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String hashedPassword) {
        return ENCODER.matches(rawPassword, hashedPassword);
    }
}
