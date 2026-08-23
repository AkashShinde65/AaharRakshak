package com.aaharrakshak.observability;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 20)
public class ErrorMonitoringFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(ErrorMonitoringFilter.class);

    private final MeterRegistry meterRegistry;
    private final boolean enabled;

    public ErrorMonitoringFilter(
            MeterRegistry meterRegistry,
            @Value("${app.monitoring.error-metrics-enabled:true}") boolean enabled) {
        this.meterRegistry = meterRegistry;
        this.enabled = enabled;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !enabled
                || path.equals("/api/v1/health")
                || path.startsWith("/actuator/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
            if (response.getStatus() >= 500) {
                record(request, response.getStatus(), "none");
            }
        } catch (ServletException | IOException | RuntimeException ex) {
            record(request, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getClass().getSimpleName());
            log.error("Unhandled API error monitored for {} {}", request.getMethod(), normalizePath(request), ex);
            throw ex;
        }
    }

    private void record(HttpServletRequest request, int status, String exception) {
        Counter.builder("aaharrakshak.http.server.errors")
                .description("Server-side errors observed by the local AaharRakshak error monitor")
                .tag("method", request.getMethod())
                .tag("uri", normalizePath(request))
                .tag("status", String.valueOf(status))
                .tag("exception", exception)
                .register(meterRegistry)
                .increment();
    }

    private String normalizePath(HttpServletRequest request) {
        return request.getRequestURI()
                .replaceAll("/\\d+", "/{id}")
                .replaceAll("/ARK-[^/]+", "/{ticket}")
                .replaceAll("/LAB-[^/]+", "/{report}");
    }
}
