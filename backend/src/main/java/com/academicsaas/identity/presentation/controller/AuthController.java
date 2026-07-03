package com.academicsaas.identity.presentation.controller;

import com.academicsaas.identity.application.usecase.AuthenticateUserUseCase;
import com.academicsaas.identity.application.usecase.RegisterInstitutionUseCase;
import com.academicsaas.identity.application.usecase.RegisterUserByDirectorUseCase;
import com.academicsaas.identity.infrastructure.security.JwtService;
import com.academicsaas.identity.presentation.dto.LoginRequest;
import com.academicsaas.identity.presentation.dto.LoginResponse;
import com.academicsaas.identity.presentation.dto.RefreshTokenRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticateUserUseCase authenticateUserUseCase;
    private final RegisterInstitutionUseCase registerInstitutionUseCase;
    private final RegisterUserByDirectorUseCase registerUserByDirectorUseCase;
    private final JwtService jwtService;

    public AuthController(
        AuthenticateUserUseCase authenticateUserUseCase,
        RegisterInstitutionUseCase registerInstitutionUseCase,
        RegisterUserByDirectorUseCase registerUserByDirectorUseCase,
        JwtService jwtService
    ) {
        this.authenticateUserUseCase = authenticateUserUseCase;
        this.registerInstitutionUseCase = registerInstitutionUseCase;
        this.registerUserByDirectorUseCase = registerUserByDirectorUseCase;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        var authResult = authenticateUserUseCase.execute(
            new AuthenticateUserUseCase.Request(request.email(), request.password(), request.institutionCode()));

        var accessToken = jwtService.createAccessToken(
            authResult.userId(), authResult.email(), authResult.roles());
        var refreshToken = jwtService.createRefreshToken();

        return ResponseEntity.ok(new LoginResponse(
            accessToken.token(),
            refreshToken.token(),
            authResult.userId(),
            authResult.email(),
            authResult.fullName(),
            authResult.roles(),
            authResult.institutionId(),
            authResult.institutionName(),
            authResult.institutionCode()
        ));
    }

    @PostMapping("/register-institution")
    public ResponseEntity<Map<String, Object>> registerInstitution(@Valid @RequestBody RegisterInstitutionUseCase.Request request) {
        var result = registerInstitutionUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(Map.of(
                "userId", result.userId(),
                "email", result.email(),
                "fullName", result.fullName(),
                "institutionId", result.institutionId(),
                "institutionName", result.institutionName(),
                "institutionCode", result.institutionCode()
            ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        var claims = jwtService.validateToken(request.refreshToken());
        var userId = claims.getSubject();
        var email = claims.get("email", String.class);
        @SuppressWarnings("unchecked")
        var roles = (List<String>) claims.get("roles");

        var newAccessToken = jwtService.createAccessToken(userId, email, roles);
        var newRefreshToken = jwtService.createRefreshToken();

        return ResponseEntity.ok(Map.of(
            "accessToken", newAccessToken.token(),
            "refreshToken", newRefreshToken.token()
        ));
    }

    @PostMapping("/register-user")
    public ResponseEntity<Map<String, Object>> registerUser(@Valid @RequestBody RegisterUserByDirectorUseCase.Request request) {
        var result = registerUserByDirectorUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(Map.of(
                "userId", result.userId(),
                "email", result.email(),
                "fullName", result.fullName(),
                "temporaryPassword", result.temporaryPassword()
            ));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent().build();
    }
}
