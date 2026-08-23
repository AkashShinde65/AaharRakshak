package com.aaharrakshak.mobile.ocr

import android.net.Uri

interface OcrAdapter {
    suspend fun extractPackageDetails(imageUri: Uri?): OcrFoodDetails
    suspend fun extractReceiptDetails(receiptUri: Uri?): ReceiptOcrDetails
}

data class OcrFoodDetails(
    val productName: String?,
    val companyName: String?,
    val fssaiLicenceNumber: String?,
    val batchNumber: String?,
    val expiryDate: String?,
    val confidence: Double,
    val warning: String
)

data class ReceiptOcrDetails(
    val receiptToken: String,
    val merchantName: String?,
    val purchasedAt: String?,
    val confidence: Double
)

class MockOcrAdapter : OcrAdapter {
    override suspend fun extractPackageDetails(imageUri: Uri?): OcrFoodDetails =
        OcrFoodDetails(
            productName = "Demo Turmeric Powder",
            companyName = "Demo Foods Private Limited",
            fssaiLicenceNumber = "12345678901234",
            batchNumber = "TUR-2026-001",
            expiryDate = "2027-01-14",
            confidence = 0.84,
            warning = "Review every scanned field before submission. The image cannot prove adulteration."
        )

    override suspend fun extractReceiptDetails(receiptUri: Uri?): ReceiptOcrDetails =
        ReceiptOcrDetails(
            receiptToken = "mock-receipt-verified",
            merchantName = "Demo Foods Store",
            purchasedAt = "2026-01-01T10:00:00Z",
            confidence = 0.88
        )
}
