package com.aaharrakshak.intelligence;

public interface NotificationChannelAdapter {

    boolean supports(NotificationChannel channel);

    void send(AlertOutbox alert);
}
