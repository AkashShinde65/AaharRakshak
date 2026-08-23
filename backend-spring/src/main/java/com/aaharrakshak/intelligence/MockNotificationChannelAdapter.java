package com.aaharrakshak.intelligence;

import org.springframework.stereotype.Component;

@Component
public class MockNotificationChannelAdapter implements NotificationChannelAdapter {

    @Override
    public boolean supports(NotificationChannel channel) {
        return channel == NotificationChannel.IN_APP
                || channel == NotificationChannel.EMAIL
                || channel == NotificationChannel.PUSH
                || channel == NotificationChannel.SMS;
    }

    @Override
    public void send(AlertOutbox alert) {
        // Academic demo adapter: record-only delivery for in-app, email, push and SMS.
    }
}
