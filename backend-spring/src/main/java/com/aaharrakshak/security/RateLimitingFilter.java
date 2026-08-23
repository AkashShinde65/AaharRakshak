package com.aaharrakshak.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final boolean enabled;
    private final int maxRequests;
    private final Duration window;
    private final Clock clock;
    private final Map<String, ClientWindow> windows = new ConcurrentHashMap<>();

    @Autowired
    public RateLimitingFilter(
            @Value("${app.security.rate-limit.enabled:true}") boolean enabled,
            @Value("${app.security.rate-limit.max-requests:120}") int maxRequests,
            @Value("${app.security.rate-limit.window-seconds:60}") long windowSeconds) {
        this(enabled, maxRequests, Duration.ofSeconds(windowSeconds), Clock.systemUTC());
    }

    RateLimitingFilter(boolean enabled, int maxRequests, Duration window, Clock clock) {
        this.enabled = enabled;
        this.maxRequests = maxRequests;
        this.window = window;
        this.clock = clock;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !enabled
                || path.equals("/api/v1/health")
                || path.startsWith("/actuator/")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/ws/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {
        String key = clientKey(request);
        ClientWindow clientWindow = windows.computeIfAbsent(key, ignored -> new ClientWindow(clock.instant()));
        if (!clientWindow.allow(clock.instant(), window, maxRequests)) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Too many requests. Please retry after the rate-limit window.\"}");
            return;
        }
        filterChain.doFilter(request, response);
    }

    private String clientKey(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        String ip = forwardedFor == null || forwardedFor.isBlank()
                ? request.getRemoteAddr()
                : forwardedFor.split(",")[0].trim();
        return ip + ":" + request.getMethod() + ":" + request.getRequestURI();
    }

    private static class ClientWindow {
        private Instant startedAt;
        private final AtomicInteger count = new AtomicInteger();

        ClientWindow(Instant startedAt) {
            this.startedAt = startedAt;
        }

        synchronized boolean allow(Instant now, Duration window, int maxRequests) {
            if (Duration.between(startedAt, now).compareTo(window) >= 0) {
                startedAt = now;
                count.set(0);
            }
            return count.incrementAndGet() <= maxRequests;
        }
    }
}
