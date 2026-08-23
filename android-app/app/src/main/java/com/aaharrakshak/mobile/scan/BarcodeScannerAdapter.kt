package com.aaharrakshak.mobile.scan

interface BarcodeScannerAdapter {
    suspend fun scan(): BarcodeScanResult
}

data class BarcodeScanResult(
    val rawValue: String,
    val format: String,
    val confidence: Double
)

class MockBarcodeScannerAdapter : BarcodeScannerAdapter {
    override suspend fun scan(): BarcodeScanResult =
        BarcodeScanResult(rawValue = "8901234567890", format = "GTIN_13", confidence = 0.98)
}
