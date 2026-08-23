package com.aaharrakshak.intelligence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

class AlertWebSocketSessionRegistryTest {

    @Test
    void sendsOnlyToAuthenticatedUserSessions() throws Exception {
        AlertWebSocketSessionRegistry registry = new AlertWebSocketSessionRegistry();
        WebSocketSession session = mock(WebSocketSession.class);
        when(session.isOpen()).thenReturn(true);

        registry.add(7L, session);
        registry.send(7L, "{\"type\":\"SLA_ESCALATION\"}");

        ArgumentCaptor<TextMessage> message = ArgumentCaptor.forClass(TextMessage.class);
        verify(session).sendMessage(message.capture());
        assertThat(message.getValue().getPayload()).isEqualTo("{\"type\":\"SLA_ESCALATION\"}");
    }
}
