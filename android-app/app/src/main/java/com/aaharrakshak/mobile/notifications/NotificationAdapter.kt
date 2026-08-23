package com.aaharrakshak.mobile.notifications

interface NotificationAdapter {
    suspend fun registerForPush(): NotificationRegistration
}

data class NotificationRegistration(
    val channel: String,
    val token: String,
    val mock: Boolean
)

class MockNotificationAdapter : NotificationAdapter {
    override suspend fun registerForPush(): NotificationRegistration =
        NotificationRegistration(
            channel = "MOCK_PUSH",
            token = "mock-device-token",
            mock = true
        )
}
