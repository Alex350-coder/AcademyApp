package com.academicsaas.shared.security.ratelimit;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class RateLimitingFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(RateLimitingFilter.class);
    private final Map<String, UserRequestCounter> counters = new ConcurrentHashMap<>();

    static final int LOGIN_MAX_REQUESTS = 20;
    static final int LOGIN_WINDOW_SECONDS = 60;
    static final int GENERAL_MAX_REQUESTS = 500;
    static final int GENERAL_WINDOW_SECONDS = 60;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        var httpRequest = (HttpServletRequest) request;
        var httpResponse = (HttpServletResponse) response;
        var path = httpRequest.getRequestURI();
        var ip = getClientIP(httpRequest);

        if (path.contains("/api/v1/auth/login") || path.contains("/api/v1/auth/refresh")) {
            if (isRateLimited(ip + ":login", LOGIN_MAX_REQUESTS, LOGIN_WINDOW_SECONDS)) {
                log.warn("Rate limit exceeded for login from IP: {}", maskIP(ip));
                httpResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                httpResponse.setContentType("application/json");
                httpResponse.getWriter().write(
                    "{\"errorCode\":\"RATE_LIMIT\"," +
                    "\"message\":\"Demasiadas solicitudes. Intente nuevamente en 60 segundos.\"," +
                    "\"status\":429}");
                return;
            }
        } else if (path.startsWith("/api/v1/")) {
            if (isRateLimited(ip + ":general", GENERAL_MAX_REQUESTS, GENERAL_WINDOW_SECONDS)) {
                httpResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                httpResponse.setContentType("application/json");
                httpResponse.getWriter().write(
                    "{\"errorCode\":\"RATE_LIMIT\"," +
                    "\"message\":\"Demasiadas solicitudes. Intente nuevamente.\"," +
                    "\"status\":429}");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    boolean isRateLimited(String key, int maxRequests, int windowSeconds) {
        var counter = counters.computeIfAbsent(key, k -> new UserRequestCounter());
        synchronized (counter) {
            long now = System.currentTimeMillis();
            if (now - counter.windowStart > windowSeconds * 1000L) {
                counter.windowStart = now;
                counter.count = 0;
            }
            counter.count++;
            return counter.count > maxRequests;
        }
    }

    private String getClientIP(HttpServletRequest request) {
        var xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String maskIP(String ip) {
        if (ip == null) {
            return "unknown";
        }
        var lastDot = ip.lastIndexOf('.');
        if (lastDot > 0) {
            return ip.substring(0, lastDot) + ".xxx";
        }
        return "xxx.xxx.xxx.xxx";
    }

    static class UserRequestCounter {
        long windowStart = System.currentTimeMillis();
        int count = 0;
    }
}
