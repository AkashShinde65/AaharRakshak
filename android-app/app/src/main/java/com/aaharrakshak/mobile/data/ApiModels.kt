package com.aaharrakshak.mobile.data

enum class RoleName {
    CITIZEN,
    COMPANY,
    FOOD_INSPECTOR,
    LABORATORY_OFFICER,
    DISTRICT_ESCALATION_OFFICER,
    CENTRAL_ADMINISTRATOR
}

enum class ComplaintType {
    PACKAGED_FOOD,
    PREPARED_DISH
}

enum class ComplaintCategory {
    SUSPECTED_ADULTERATION,
    MISLABELING,
    EXPIRED_PRODUCT,
    HYGIENE_ISSUE,
    FOREIGN_OBJECT,
    OTHER
}

enum class EvidenceType {
    PRODUCT_LABEL_PHOTO,
    LICENCE_LABEL_PHOTO,
    DISH_IMAGE,
    VENDOR_IMAGE,
    VIDEO,
    RECEIPT_FILE,
    OTHER
}

data class RegisterCitizenRequest(
    val fullName: String,
    val email: String,
    val mobileNumber: String,
    val password: String
)

data class LoginRequest(
    val identifier: String,
    val password: String
)

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String,
    val expiresInSeconds: Long,
    val userId: Long,
    val fullName: String,
    val status: String,
    val roles: Set<RoleName>
)

data class RegistrationResponse(
    val userId: Long,
    val companyId: Long?,
    val message: String,
    val userStatus: String,
    val companyStatus: String?,
    val roles: Set<RoleName>
)

data class OtpRequest(
    val identifier: String,
    val channel: String
)

data class VerifyOtpRequest(
    val identifier: String,
    val channel: String,
    val code: String
)

data class VerificationResponse(
    val verified: Boolean,
    val verificationToken: String?,
    val message: String?
)

data class FileMetadataDto(
    val type: EvidenceType? = null,
    val objectKey: String,
    val originalFileName: String?,
    val contentType: String,
    val sizeBytes: Long,
    val checksumSha256: String,
    val capturedAt: String? = null
)

data class GpsLocationDto(
    val consentAccepted: Boolean,
    val latitude: Double?,
    val longitude: Double?,
    val address: String?
)

data class ComplaintDraftRequest(
    val complaintType: ComplaintType,
    val category: ComplaintCategory,
    val scannedBarcode: String? = null,
    val productId: Long? = null,
    val batchId: Long? = null,
    val detectedProductName: String? = null,
    val detectedCompanyName: String? = null,
    val detectedFssaiLicenceNumber: String? = null,
    val detectedBatchNumber: String? = null,
    val detectedExpiryDate: String? = null,
    val confirmedProductName: String? = null,
    val confirmedCompanyName: String? = null,
    val confirmedFssaiLicenceNumber: String? = null,
    val confirmedBatchNumber: String? = null,
    val confirmedExpiryDate: String? = null,
    val vendorName: String? = null,
    val vendorAddress: String? = null,
    val description: String? = null,
    val location: GpsLocationDto? = null,
    val evidence: List<FileMetadataDto> = emptyList()
)

data class ComplaintResponse(
    val complaintId: Long,
    val ticketNumber: String?,
    val complaintType: ComplaintType,
    val category: ComplaintCategory,
    val status: String,
    val confirmedProductName: String?,
    val confirmedCompanyName: String?,
    val confirmedBatchNumber: String?,
    val description: String?,
    val evidence: List<EvidenceResponse> = emptyList(),
    val statusHistory: List<ComplaintStatusHistoryResponse> = emptyList()
)

data class EvidenceResponse(
    val id: Long,
    val type: EvidenceType,
    val originalFileName: String?,
    val contentType: String,
    val sizeBytes: Long,
    val checksumSha256: String,
    val capturedAt: String?
)

data class ComplaintStatusHistoryResponse(
    val status: String,
    val note: String?,
    val changedAt: String
)

data class PublicProductResponse(
    val productId: Long,
    val name: String,
    val brand: String?,
    val category: String?,
    val manufacturerName: String?,
    val primaryBarcode: String?,
    val barcodes: List<String> = emptyList(),
    val companyId: Long?,
    val companyName: String?,
    val tradeName: String?,
    val companyStatus: String?
)

data class PublicComplaintStatusResponse(
    val ticketNumber: String,
    val complaintType: String?,
    val status: String,
    val category: String?,
    val companyName: String?,
    val productName: String?,
    val batchNumber: String?,
    val district: String?,
    val submittedAt: String?,
    val updatedAt: String?,
    val publishedReports: List<PublicLabReportResponse> = emptyList()
)

data class PublicLabReportResponse(
    val reportNumber: String,
    val ticketNumber: String?,
    val outcome: String,
    val resultSummary: String?,
    val publishedAt: String?,
    val productName: String?,
    val companyName: String?,
    val batchNumber: String?,
    val district: String?,
    val results: List<PublicLabResultResponse> = emptyList()
)

data class PublicLabResultResponse(
    val parameterName: String,
    val resultValue: String?,
    val unit: String?,
    val permissibleLimit: String?,
    val compliant: Boolean?,
    val remarks: String?
)

data class PublicLicenceStatusResponse(
    val licenceNumber: String,
    val companyName: String?,
    val companyStatus: String?,
    val registryBackedStatus: String?,
    val simulatedAdministrativeStatus: String?,
    val validTo: String?,
    val safetyNote: String?
)

data class PublicBatchStatusResponse(
    val batchNumber: String,
    val productName: String?,
    val companyName: String?,
    val platformStatus: String?,
    val manufacturedOn: String?,
    val expiresOn: String?,
    val safetyNote: String?
)

data class SafetyAlertResponse(
    val alertId: Long,
    val title: String,
    val message: String,
    val severity: String?,
    val companyName: String?,
    val productName: String?,
    val batchNumber: String?,
    val location: String?,
    val publishedAt: String?
)

data class AlertOutboxResponse(
    val alertId: Long,
    val userId: Long?,
    val eventType: String?,
    val channel: String,
    val subject: String,
    val body: String,
    val status: String,
    val retryCount: Int?,
    val createdAt: String?
)

data class TrustScoreResponse(
    val companyId: Long,
    val companyName: String?,
    val score: Double,
    val riskLevel: String,
    val inspectionPoints: Double?,
    val labPoints: Double?,
    val recallPoints: Double?,
    val reviewPoints: Double?,
    val reviewCount: Int,
    val explanation: String?,
    val rawComplaintFairnessNote: String?,
    val recalculatedAt: String?
)

data class HotspotResponse(
    val hotspotId: Long?,
    val hotspotKey: String?,
    val district: String?,
    val relatedKey: String?,
    val productOrVendor: String?,
    val riskLevel: String,
    val complaintCount: Int,
    val radiusKm: Double,
    val centerLatitude: Double,
    val centerLongitude: Double,
    val windowStart: String?,
    val windowEnd: String?,
    val detectedAt: String?,
    val complaintNumbers: List<String> = emptyList(),
    val privacyNote: String?
)

data class VendorReviewRequest(
    val companyId: Long,
    val productId: Long?,
    val batchId: Long?,
    val rating: Int,
    val reviewText: String,
    val receipt: ReceiptMetadataDto
)

data class ReceiptMetadataDto(
    val objectKey: String,
    val originalFileName: String,
    val contentType: String,
    val sizeBytes: Long,
    val checksumSha256: String
)

data class VendorReviewResponse(
    val reviewId: Long,
    val companyId: Long,
    val rating: Int,
    val receiptVerified: Boolean,
    val receiptVerificationToken: String?,
    val createdAt: String?,
    val moderationNote: String?
)
