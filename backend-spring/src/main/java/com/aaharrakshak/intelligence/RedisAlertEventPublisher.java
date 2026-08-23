package com.aaharrakshak.intelligence;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisAlertEventPublisher implements AlertEventPublisher {

    private static final String CHANNEL = "aaharrakshak.alerts";

    private final ObjectProvider<StringRedisTemplate> redisTemplateProvider;
    private final ObjectMapper objectMapper;

    public RedisAlertEventPublisher(ObjectProvider<StringRedisTemplate> redisTemplateProvider, ObjectMapper objectMapper) {
        this.redisTemplateProvider = redisTemplateProvider;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(AlertOutbox alert) {
        StringRedisTemplate redisTemplate = redisTemplateProvider.getIfAvailable();
        if (redisTemplate == null) {
            return;
        }
        try {
            redisTemplate.convertAndSend(CHANNEL, objectMapper.writeValueAsString(Map.of(
                    "alertId", alert.getId(),
                    "userId", alert.getUser() == null ? "" : alert.getUser().getId(),
                    "eventType", alert.getEventType(),
                    "channel", alert.getChannel().name(),
                    "subject", alert.getSubject(),
                    "body", alert.getBody())));
        } catch (RuntimeException | java.io.IOException ignored) {
            // The database outbox remains the durable source when Redis is unavailable.
        }
    }
}
