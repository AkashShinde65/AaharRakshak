package com.aaharrakshak.intelligence;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final AlertWebSocketHandler alertWebSocketHandler;
    private final JwtAlertHandshakeInterceptor jwtAlertHandshakeInterceptor;

    public WebSocketConfig(
            AlertWebSocketHandler alertWebSocketHandler,
            JwtAlertHandshakeInterceptor jwtAlertHandshakeInterceptor) {
        this.alertWebSocketHandler = alertWebSocketHandler;
        this.jwtAlertHandshakeInterceptor = jwtAlertHandshakeInterceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(alertWebSocketHandler, "/ws/alerts")
                .addInterceptors(jwtAlertHandshakeInterceptor)
                .setAllowedOriginPatterns("*");
    }
}
