package com.aaharrakshak.intelligence;

import com.aaharrakshak.investigation.Action;
import com.aaharrakshak.investigation.ActionType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MockExternalAccountEventPublisher implements ExternalAccountEventPublisher {

    private final MockExternalEventRepository eventRepository;
    private final ObjectMapper objectMapper;

    public MockExternalAccountEventPublisher(MockExternalEventRepository eventRepository, ObjectMapper objectMapper) {
        this.eventRepository = eventRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public void publish(Action action) {
        if (action.getType() == ActionType.WARNING) {
            return;
        }
        String eventType = switch (action.getType()) {
            case BATCH_RECALL -> "MOCK_BATCH_RECALLED";
            case TEMPORARY_SUSPENSION -> "MOCK_LICENCE_TEMPORARILY_SUSPENDED";
            case CANCELLATION -> "MOCK_LICENCE_CANCELLED";
            case WARNING -> "MOCK_WARNING";
        };
        String targetType = action.getType() == ActionType.BATCH_RECALL ? "BATCH" : "LICENCE";
        String targetId = action.getType() == ActionType.BATCH_RECALL && action.getComplaint().getBatch() != null
                ? action.getComplaint().getBatch().getBatchNumber()
                : action.getCompany().getLegalName();
        eventRepository.save(new MockExternalEvent(
                eventType,
                targetType,
                targetId,
                toJson(Map.of(
                        "actionNumber", action.getActionNumber(),
                        "simulated", true,
                        "storefrontIntegration", "mock-only",
                        "deliveryIntegration", "mock-only",
                        "paymentIntegration", "mock-only",
                        "safetyNote", "No real external account or service is disabled."))));
    }

    private String toJson(Map<String, Object> payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Could not serialize mock external event", ex);
        }
    }
}
