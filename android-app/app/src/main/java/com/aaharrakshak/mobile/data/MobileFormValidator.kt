package com.aaharrakshak.mobile.data

object MobileFormValidator {
    private val fssaiRegex = Regex("^\\d{14}$")
    private val barcodeRegex = Regex("^(\\d{8}|\\d{12}|\\d{13}|\\d{14})$")
    private val sha256Regex = Regex("^[a-fA-F0-9]{64}$")

    fun validateFssaiLicence(value: String?): Boolean =
        value.isNullOrBlank() || fssaiRegex.matches(value)

    fun validateBarcode(value: String?): Boolean =
        value.isNullOrBlank() || barcodeRegex.matches(value)

    fun validateChecksum(value: String): Boolean = sha256Regex.matches(value)

    fun complaintCanSubmit(request: ComplaintDraftRequest): Boolean {
        val hasDescription = !request.description.isNullOrBlank()
        val hasConsentIfGpsProvided = request.location?.let {
            it.latitude == null && it.longitude == null || it.consentAccepted
        } ?: true
        val hasIdentity = when (request.complaintType) {
            ComplaintType.PACKAGED_FOOD -> !request.confirmedProductName.isNullOrBlank() ||
                !request.scannedBarcode.isNullOrBlank()
            ComplaintType.PREPARED_DISH -> !request.vendorName.isNullOrBlank() ||
                !request.confirmedProductName.isNullOrBlank()
        }
        return hasDescription &&
            hasIdentity &&
            hasConsentIfGpsProvided &&
            validateFssaiLicence(request.confirmedFssaiLicenceNumber) &&
            validateBarcode(request.scannedBarcode)
    }

    fun chemicalAdulterationDisclaimer(): String =
        "Images may suggest a complaint category, but laboratory confirmation is mandatory for chemical adulteration."
}
