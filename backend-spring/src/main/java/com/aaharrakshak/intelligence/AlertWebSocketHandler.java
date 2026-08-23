package com.aaharrakshak.intelligence;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class AlertWebSocketHandler extends TextWebSocketHandler {

    private final AlertWebSocketSessionRegistry sessionRegistry;

    public AlertWebSocketHandler(AlertWebSocketSessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long userId = userId(session);
        sessionRegistry.add(userId, session);
        session.sendMessage(new TextMessage("{\"type\":\"CONNECTED\",\"message\":\"AaharRakshak alert socket authenticated\"}"));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessionRegistry.remove(userId(session), session);
    }

    private Long userId(WebSocketSession session) {
        return (Long) session.getAttributes().get(JwtAlertHandshakeInterceptor.USER_ID_ATTRIBUTE);
    }
}
