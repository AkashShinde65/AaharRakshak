package com.aaharrakshak.mobile.data

import com.aaharrakshak.mobile.network.AaharRakshakApi
import com.aaharrakshak.mobile.notifications.NotificationAdapter
import com.aaharrakshak.mobile.ocr.OcrAdapter
import com.aaharrakshak.mobile.ocr.OcrFoodDetails
import com.aaharrakshak.mobile.scan.BarcodeScanResult
import com.aaharrakshak.mobile.scan.BarcodeScannerAdapter
import com.aaharrakshak.mobile.security.SecureTokenStore
import kotlinx.coroutines.flow.Flow

class MobileRepository(
    private val tokenStore: SecureTokenStore,
    private val draftDao: ComplaintDraftDao,
    private val api: AaharRakshakApi,
    private val barcodeScanner: BarcodeScannerAdapter,
    private val ocrAdapter: OcrAdapter,
    private val notificationAdapter: NotificationAdapter
) {
    fun observeOfflineDrafts(): Flow<List<OfflineComplaintDraftEntity>> = draftDao.observeDrafts()

    suspend fun registerCitizen(fullName: String, email: String, mobile: String, password: String): RegistrationResponse =
        api.registerCitizen(RegisterCitizenRequest(fullName, email, mobile, password))

    suspend fun login(identifier: String, password: String): AuthResponse {
        val response = api.login(LoginRequest(identifier, password))
        tokenStore.saveTokens(response.accessToken, response.refreshToken)
        return response
    }

    suspend fun requestOtp(destination: String, channel: String = "MOBILE"): VerificationResponse =
        api.requestOtp(OtpRequest(destination, channel))

    suspend fun verifyOtp(destination: String, code: String, channel: String = "MOBILE"): VerificationResponse =
        api.verifyOtp(VerifyOtpRequest(destination, channel, code))

    suspend fun scanBarcode(): BarcodeScanResult = barcodeScanner.scan()

    suspend fun scanPackageImage(): OcrFoodDetails = ocrAdapter.extractPackageDetails(null)

    suspend fun productByBarcode(barcode: String): PublicProductResponse = api.productByBarcode(barcode)

    suspend fun searchProducts(query: String): List<PublicProductResponse> = api.searchProducts(query)

    suspend fun saveOfflineDraft(request: ComplaintDraftRequest): Long =
        draftDao.insert(request.toOfflineEntity())

    suspend fun createAndSubmitComplaint(request: ComplaintDraftRequest): ComplaintResponse {
        require(MobileFormValidator.complaintCanSubmit(request)) {
            "Complaint needs confirmed details, description, valid identifiers and GPS consent when location is used."
        }
        val draft = api.createComplaintDraft(request)
        return api.submitComplaint(draft.complaintId)
    }

    suspend fun complaintHistory(): List<ComplaintResponse> = api.myComplaints()

    suspend fun publicComplaintStatus(ticketNumber: String): PublicComplaintStatusResponse =
        api.publicComplaintStatus(ticketNumber)

    suspend fun publicLabReport(reportNumber: String): PublicLabReportResponse =
        api.publicLabReport(reportNumber)

    suspend fun publicLicenceStatus(licenceNumber: String): PublicLicenceStatusResponse =
        api.publicLicenceStatus(licenceNumber)

    suspend fun publicBatchStatus(batchNumber: String): PublicBatchStatusResponse =
        api.publicBatchStatus(batchNumber)

    suspend fun safetyAlerts(): List<SafetyAlertResponse> = api.safetyAlerts()

    suspend fun citizenAlerts(): List<AlertOutboxResponse> = api.citizenAlerts()

    suspend fun trustScore(companyId: Long): TrustScoreResponse = api.trustScore(companyId)

    suspend fun submitReview(request: VendorReviewRequest): VendorReviewResponse = api.submitReview(request)

    suspend fun notificationRegistration() = notificationAdapter.registerForPush()

    suspend fun officialOrMockHotspots(district: String?): List<HotspotResponse> =
        runCatching { api.officialHotspots(district) }.getOrElse {
            listOf(
                HotspotResponse(
                    hotspotId = null,
                    hotspotKey = "mock-mobile-hotspot",
                    district = district ?: "Demo district",
                    relatedKey = "mock",
                    productOrVendor = "Aggregate public-safety demo",
                    riskLevel = "MEDIUM",
                    complaintCount = 6,
                    radiusKm = 2.0,
                    centerLatitude = 18.52043,
                    centerLongitude = 73.85674,
                    windowStart = null,
                    windowEnd = null,
                    detectedAt = null,
                    complaintNumbers = emptyList(),
                    privacyNote = "Mock aggregate hotspot. Citizen-level locations are hidden."
                )
            )
        }

    fun logout() = tokenStore.clear()
}
