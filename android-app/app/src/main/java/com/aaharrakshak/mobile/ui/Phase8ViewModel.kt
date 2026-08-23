package com.aaharrakshak.mobile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaharrakshak.mobile.data.ComplaintCategory
import com.aaharrakshak.mobile.data.ComplaintDraftRequest
import com.aaharrakshak.mobile.data.ComplaintResponse
import com.aaharrakshak.mobile.data.ComplaintType
import com.aaharrakshak.mobile.data.FileMetadataDto
import com.aaharrakshak.mobile.data.GpsLocationDto
import com.aaharrakshak.mobile.data.MobileFormValidator
import com.aaharrakshak.mobile.data.MobileRepository
import com.aaharrakshak.mobile.data.OfflineComplaintDraftEntity
import com.aaharrakshak.mobile.data.PublicProductResponse
import com.aaharrakshak.mobile.data.SafetyAlertResponse
import com.aaharrakshak.mobile.data.TrustScoreResponse
import com.aaharrakshak.mobile.data.EvidenceType
import com.aaharrakshak.mobile.data.HotspotResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class Phase8ViewModel(
    private val repository: MobileRepository
) : ViewModel() {
    val offlineDrafts: StateFlow<List<OfflineComplaintDraftEntity>> =
        repository.observeOfflineDrafts()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _state = MutableStateFlow(Phase8UiState())
    val state: StateFlow<Phase8UiState> = _state.asStateFlow()

    fun updateScreen(screen: MobileScreen) {
        _state.value = _state.value.copy(screen = screen, error = null, message = null)
    }

    fun updateAuth(identifier: String? = null, password: String? = null, name: String? = null, mobile: String? = null) {
        _state.value = _state.value.copy(
            identifier = identifier ?: _state.value.identifier,
            password = password ?: _state.value.password,
            fullName = name ?: _state.value.fullName,
            mobile = mobile ?: _state.value.mobile
        )
    }

    fun register() = launchWithStatus {
        repository.registerCitizen(
            fullName = _state.value.fullName,
            email = _state.value.identifier,
            mobile = _state.value.mobile,
            password = _state.value.password
        )
        _state.value = _state.value.copy(message = "Registered. Use mock OTP 123456 if verification is requested, then login.")
    }

    fun login() = launchWithStatus {
        val response = repository.login(_state.value.identifier, _state.value.password)
        _state.value = _state.value.copy(
            isAuthenticated = true,
            displayName = response.fullName,
            roles = response.roles.map { it.name },
            screen = MobileScreen.DASHBOARD,
            message = "Signed in as ${response.fullName}."
        )
        repository.notificationRegistration()
    }

    fun requestOtp() = launchWithStatus {
        repository.requestOtp(_state.value.identifier)
        _state.value = _state.value.copy(message = "Mock OTP sent. Development code is 123456.")
    }

    fun verifyOtp(code: String) = launchWithStatus {
        val verification = repository.verifyOtp(_state.value.identifier, code)
        _state.value = _state.value.copy(message = verification.message ?: "OTP verified.")
    }

    fun scanBarcodeThenLookup() = launchWithStatus {
        val scan = repository.scanBarcode()
        _state.value = _state.value.copy(barcode = scan.rawValue, message = "Scanned ${scan.rawValue}. Looking up product...")
        val product = repository.productByBarcode(scan.rawValue)
        _state.value = _state.value.copy(
            productName = product.name,
            companyOrVendor = product.companyName.orEmpty(),
            productLookupResults = listOf(product),
            message = "Barcode matched ${product.name}. Confirm details before complaint submission."
        )
    }

    fun scanPackageImage() = launchWithStatus {
        val ocr = repository.scanPackageImage()
        _state.value = _state.value.copy(
            productName = ocr.productName.orEmpty(),
            companyOrVendor = ocr.companyName.orEmpty(),
            fssaiLicenceNumber = ocr.fssaiLicenceNumber.orEmpty(),
            batchNumber = ocr.batchNumber.orEmpty(),
            expiryDate = ocr.expiryDate.orEmpty(),
            message = ocr.warning
        )
    }

    fun updateComplaintForm(
        productName: String? = null,
        companyOrVendor: String? = null,
        licence: String? = null,
        batch: String? = null,
        expiry: String? = null,
        description: String? = null,
        address: String? = null,
        gpsConsent: Boolean? = null,
        preparedDish: Boolean? = null
    ) {
        _state.value = _state.value.copy(
            productName = productName ?: _state.value.productName,
            companyOrVendor = companyOrVendor ?: _state.value.companyOrVendor,
            fssaiLicenceNumber = licence ?: _state.value.fssaiLicenceNumber,
            batchNumber = batch ?: _state.value.batchNumber,
            expiryDate = expiry ?: _state.value.expiryDate,
            description = description ?: _state.value.description,
            address = address ?: _state.value.address,
            gpsConsent = gpsConsent ?: _state.value.gpsConsent,
            preparedDish = preparedDish ?: _state.value.preparedDish
        )
    }

    fun attachEvidence(type: EvidenceType, fileName: String) {
        val evidence = _state.value.evidence + FileMetadataDto(
            type = type,
            objectKey = "mobile/mock/${System.currentTimeMillis()}-$fileName",
            originalFileName = fileName,
            contentType = when (type) {
                EvidenceType.VIDEO -> "video/mp4"
                EvidenceType.RECEIPT_FILE -> "application/pdf"
                else -> "image/jpeg"
            },
            sizeBytes = 2048,
            checksumSha256 = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
            capturedAt = "2026-01-01T10:00:00Z"
        )
        _state.value = _state.value.copy(evidence = evidence, message = "Evidence metadata attached: $fileName.")
    }

    fun saveOfflineDraft() = launchWithStatus {
        repository.saveOfflineDraft(currentDraft())
        _state.value = _state.value.copy(message = "Saved offline draft on this device.")
    }

    fun submitComplaint() = launchWithStatus {
        val draft = currentDraft()
        if (!MobileFormValidator.complaintCanSubmit(draft)) {
            _state.value = _state.value.copy(error = "Add confirmed details, description and consent when GPS is used.")
            return@launchWithStatus
        }
        val response = repository.createAndSubmitComplaint(draft)
        _state.value = _state.value.copy(
            submittedComplaint = response,
            screen = MobileScreen.TRACKING,
            message = "Complaint submitted. Tracking number: ${response.ticketNumber}."
        )
    }

    fun loadHistory() = launchWithStatus {
        _state.value = _state.value.copy(history = repository.complaintHistory())
    }

    fun searchProducts(query: String) = launchWithStatus {
        _state.value = _state.value.copy(productQuery = query, productLookupResults = repository.searchProducts(query))
    }

    fun loadAlerts() = launchWithStatus {
        _state.value = _state.value.copy(publicAlerts = repository.safetyAlerts())
    }

    fun loadHotspots(district: String? = null) = launchWithStatus {
        _state.value = _state.value.copy(hotspots = repository.officialOrMockHotspots(district))
    }

    fun loadTrustScore(companyId: Long) = launchWithStatus {
        _state.value = _state.value.copy(trustScore = repository.trustScore(companyId))
    }

    private fun currentDraft(): ComplaintDraftRequest {
        val location = if (_state.value.gpsConsent) {
            GpsLocationDto(
                consentAccepted = true,
                latitude = 18.52043,
                longitude = 73.85674,
                address = _state.value.address.ifBlank { "Mobile consent location" }
            )
        } else {
            null
        }
        return if (_state.value.preparedDish) {
            ComplaintDraftRequest(
                complaintType = ComplaintType.PREPARED_DISH,
                category = ComplaintCategory.HYGIENE_ISSUE,
                confirmedProductName = _state.value.productName.blankToNull(),
                vendorName = _state.value.companyOrVendor.blankToNull(),
                vendorAddress = _state.value.address.blankToNull(),
                description = _state.value.description,
                location = location,
                evidence = _state.value.evidence
            )
        } else {
            ComplaintDraftRequest(
                complaintType = ComplaintType.PACKAGED_FOOD,
                category = ComplaintCategory.SUSPECTED_ADULTERATION,
                scannedBarcode = _state.value.barcode.blankToNull(),
                detectedProductName = _state.value.productName.blankToNull(),
                detectedCompanyName = _state.value.companyOrVendor.blankToNull(),
                detectedFssaiLicenceNumber = _state.value.fssaiLicenceNumber.blankToNull(),
                detectedBatchNumber = _state.value.batchNumber.blankToNull(),
                detectedExpiryDate = _state.value.expiryDate.blankToNull(),
                confirmedProductName = _state.value.productName.blankToNull(),
                confirmedCompanyName = _state.value.companyOrVendor.blankToNull(),
                confirmedFssaiLicenceNumber = _state.value.fssaiLicenceNumber.blankToNull(),
                confirmedBatchNumber = _state.value.batchNumber.blankToNull(),
                confirmedExpiryDate = _state.value.expiryDate.blankToNull(),
                description = _state.value.description,
                location = location,
                evidence = _state.value.evidence
            )
        }
    }

    private fun launchWithStatus(block: suspend () -> Unit) {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            runCatching { block() }
                .onFailure { error ->
                    _state.value = _state.value.copy(error = error.message ?: "Request failed.")
                }
            _state.value = _state.value.copy(loading = false)
        }
    }
}

private fun String.blankToNull(): String? = takeIf { it.isNotBlank() }

data class Phase8UiState(
    val screen: MobileScreen = MobileScreen.AUTH,
    val loading: Boolean = false,
    val error: String? = null,
    val message: String? = null,
    val isAuthenticated: Boolean = false,
    val displayName: String = "",
    val roles: List<String> = emptyList(),
    val identifier: String = "citizen@example.com",
    val password: String = "Password@123",
    val fullName: String = "Demo Citizen",
    val mobile: String = "9999999999",
    val barcode: String = "",
    val productName: String = "",
    val companyOrVendor: String = "",
    val fssaiLicenceNumber: String = "",
    val batchNumber: String = "",
    val expiryDate: String = "",
    val description: String = "Mobile complaint draft with citizen-confirmed details.",
    val address: String = "Pune demo market",
    val gpsConsent: Boolean = false,
    val preparedDish: Boolean = false,
    val evidence: List<FileMetadataDto> = emptyList(),
    val submittedComplaint: ComplaintResponse? = null,
    val history: List<ComplaintResponse> = emptyList(),
    val productQuery: String = "turmeric",
    val productLookupResults: List<PublicProductResponse> = emptyList(),
    val publicAlerts: List<SafetyAlertResponse> = emptyList(),
    val hotspots: List<HotspotResponse> = emptyList(),
    val trustScore: TrustScoreResponse? = null
)

enum class MobileScreen {
    AUTH,
    DASHBOARD,
    PACKAGE_COMPLAINT,
    DISH_COMPLAINT,
    DRAFTS,
    TRACKING,
    LOOKUP,
    ALERTS,
    HOTSPOTS,
    TRUST,
    PRIVACY
}
