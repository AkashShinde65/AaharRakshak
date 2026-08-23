package com.aaharrakshak.intelligence;

public interface AlertEventPublisher {

    void publish(AlertOutbox alert);
}
