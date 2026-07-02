package com.academicsaas.identity.infrastructure.security;

import com.academicsaas.shared.api.ApiError;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    private static final List<String> PUBLIC_PATHS = List.of(
        "/api/v1/auth/login",
        "/api/v1/auth/refresh",
        "/api/v1/auth/register-institution",
        "/api/v1/institutions",
        "/swagger-ui",
        "/api-docs",
        "/actuator"
    );

    public JwtAuthenticationFilter(JwtService jwtService, ObjectMapper objectMapper) {
        this.jwtService = jwtService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        var path = request.getRequestURI();
        if (PUBLIC_PATHS.stream().anyMatch(path::startsWith)) {
            filterChain.doFilter(request, response);
            return;
        }

        var authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendError(response, "Missing or invalid Authorization header");
            return;
        }

        try {
            var token = authHeader.substring(7);
            var claims = jwtService.validateToken(token);

            var userId = claims.getSubject();
            var email = claims.get("email", String.class);
            var roles = claims.get("roles", List.class);

            if (userId == null) {
                sendError(response, "Invalid token: missing subject");
                return;
            }

            var authorities = roles != null
                ? ((List<String>) roles).stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role)).toList()
                : List.<SimpleGrantedAuthority>of();

            var auth = new UsernamePasswordAuthenticationToken(userId, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);

            filterChain.doFilter(request, response);
        } catch (JwtService.JwtValidationException e) {
            sendError(response, e.getMessage());
        }
    }

    private void sendError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        var error = new ApiError("TOKEN_INVALID", message, 401);
        objectMapper.writeValue(response.getWriter(), error);
    }
}
