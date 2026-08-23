package com.aaharrakshak.mobile

import com.aaharrakshak.mobile.data.AaharRakshakDatabase
import com.aaharrakshak.mobile.data.MobileRepository
import com.aaharrakshak.mobile.network.AaharRakshakApi
import com.aaharrakshak.mobile.notifications.NotificationAdapter
import com.aaharrakshak.mobile.ocr.OcrAdapter
import com.aaharrakshak.mobile.scan.BarcodeScannerAdapter
import com.aaharrakshak.mobile.security.SecureTokenStore

class AppContainer(
    tokenStore: SecureTokenStore,
    database: AaharRakshakDatabase,
    api: AaharRakshakApi,
    barcodeScanner: BarcodeScannerAdapter,
    ocrAdapter: OcrAdapter,
    notificationAdapter: NotificationAdapter
) {
    val repository = MobileRepository(
        tokenStore = tokenStore,
        draftDao = database.complaintDraftDao(),
        api = api,
        barcodeScanner = barcodeScanner,
        ocrAdapter = ocrAdapter,
        notificationAdapter = notificationAdapter
    )
}
