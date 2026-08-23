package com.aaharrakshak.mobile

import android.app.Application
import com.aaharrakshak.mobile.data.AaharRakshakDatabase
import com.aaharrakshak.mobile.network.ApiClient
import com.aaharrakshak.mobile.notifications.MockNotificationAdapter
import com.aaharrakshak.mobile.ocr.MockOcrAdapter
import com.aaharrakshak.mobile.scan.MockBarcodeScannerAdapter
import com.aaharrakshak.mobile.security.SecureTokenStore

class AaharRakshakMobileApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        val tokenStore = SecureTokenStore(this)
        val database = AaharRakshakDatabase.create(this)
        val api = ApiClient.create(BuildConfig.AAHAR_API_BASE_URL, tokenStore)
        container = AppContainer(
            tokenStore = tokenStore,
            database = database,
            api = api,
            barcodeScanner = MockBarcodeScannerAdapter(),
            ocrAdapter = MockOcrAdapter(),
            notificationAdapter = MockNotificationAdapter()
        )
    }
}
