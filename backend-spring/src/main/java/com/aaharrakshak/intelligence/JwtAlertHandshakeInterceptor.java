package com.aaharrakshak.intelligence;

import com.aaharrakshak.security.AuthenticatedUser;
import com.aaharrakshak.security.JwtClaims;
import com.aaharrakshak.security.JwtService;
import com.aaharrakshak.security.UserPrincipalService;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class JwtAlertHandshakeInterceptor implements HandshakeInterceptor {

    static final String USER_ID_ATTRIBUTE = "userId";

    private final JwtService jwtService;
    private final UserPrincipalService userPrincipalService;

    public JwtAlertHandshakeInterceptor(JwtService jwtService, UserPrincipalService userPrincipalService) {
        this.jwtService = jwtService;
        this.userPrincipalService = userPrincipalService;
    }

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) {
        String token = UriComponentsBuilder.fromUri(request.getURI())
                .build()
                .getQueryParams()
                .getFirst("access_token");
        if (token == null || token.isBlank()) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }
        try {
            JwtClaims claims = jwtService.parseAndValidate(token);
            AuthenticatedUser user = userPrincipalService.loadActiveUser(claims.userId());
            attributes.put(USER_ID_ATTRIBUTE, user.getUserId());
            return true;
        } catch (RuntimeException ex) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception) {
    }
}
