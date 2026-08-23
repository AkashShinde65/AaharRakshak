package com.aaharrakshak.mobile.network

import com.aaharrakshak.mobile.security.SecureTokenStore
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class RealtimeAlertClient(
    private val apiBaseUrl: String,
    private val tokenStore: SecureTokenStore,
    private val client: OkHttpClient = OkHttpClient()
) {
    fun connect(onMessage: (String) -> Unit, onClosed: () -> Unit = {}): WebSocket? {
        val token = tokenStore.accessToken() ?: return null
        val wsUrl = apiBaseUrl
            .trimEnd('/')
            .replace("https://", "wss://")
            .replace("http://", "ws://") + "/ws/alerts?token=$token"
        val request = Request.Builder().url(wsUrl).build()
        return client.newWebSocket(request, object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) = onMessage(text)
            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) = onClosed()
            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) = onClosed()
        })
    }
}
