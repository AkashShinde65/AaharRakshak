package com.aaharrakshak.security;

import com.aaharrakshak.user.RoleName;
import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            RateLimitingFilter rateLimitingFilter)
            throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .headers(headers -> headers
                        .contentSecurityPolicy(csp -> csp.policyDirectives(
                                "default-src 'self'; img-src 'self' data: https:; media-src 'self' data: https:; "
                                        + "script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline'; "
                                        + "connect-src 'self' ws: wss: http://localhost:* http://10.0.2.2:*"))
                        .referrerPolicy(referrer -> referrer
                                .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER))
                        .permissionsPolicyHeader(policy -> policy.policy(
                                "camera=(self), geolocation=(self), microphone=(), payment=()")))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/error",
                                "/api/v1/health",
                                "/actuator/health",
                                "/ws/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/auth/register/**",
                                "/api/v1/auth/login",
                                "/api/v1/auth/refresh",
                                "/api/v1/auth/otp/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/public/**").permitAll()
                        .requestMatchers("/api/v1/citizen/**").hasRole(RoleName.CITIZEN.name())
                        .requestMatchers("/api/v1/company/**").hasRole(RoleName.COMPANY.name())
                        .requestMatchers("/api/v1/official/licences/**")
                        .hasAnyRole(
                                RoleName.FOOD_INSPECTOR.name(),
                                RoleName.DISTRICT_ESCALATION_OFFICER.name(),
                                RoleName.CENTRAL_ADMINISTRATOR.name())
                        .requestMatchers("/api/v1/official/complaints/**")
                        .hasAnyRole(
                                RoleName.FOOD_INSPECTOR.name(),
                                RoleName.LABORATORY_OFFICER.name(),
                                RoleName.DISTRICT_ESCALATION_OFFICER.name(),
                                RoleName.CENTRAL_ADMINISTRATOR.name())
                        .requestMatchers("/api/v1/official/investigations/**")
                        .hasAnyRole(
                                RoleName.FOOD_INSPECTOR.name(),
                                RoleName.DISTRICT_ESCALATION_OFFICER.name(),
                                RoleName.CENTRAL_ADMINISTRATOR.name())
                        .requestMatchers("/api/v1/lab/investigations/**").hasRole(RoleName.LABORATORY_OFFICER.name())
                        .requestMatchers("/api/v1/official/inspectors/**").hasRole(RoleName.FOOD_INSPECTOR.name())
                        .requestMatchers("/api/v1/official/lab/**").hasRole(RoleName.LABORATORY_OFFICER.name())
                        .requestMatchers("/api/v1/official/district/**")
                        .hasRole(RoleName.DISTRICT_ESCALATION_OFFICER.name())
                        .requestMatchers("/api/v1/admin/**").hasRole(RoleName.CENTRAL_ADMINISTRATOR.name())
                        .anyRequest().authenticated())
                .addFilterBefore(rateLimitingFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(
            @Value("${app.security.cors-allowed-origins:http://localhost:5000,http://localhost:5080,http://localhost:8080,http://10.0.2.2:8080}") String allowedOrigins) {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isBlank())
                .toList());
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setExposedHeaders(Arrays.asList("Location"));
        configuration.setAllowCredentials(false);
        configuration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
