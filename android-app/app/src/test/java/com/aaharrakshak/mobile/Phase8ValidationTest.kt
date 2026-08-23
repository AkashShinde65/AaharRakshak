package com.aaharrakshak.mobile

import com.aaharrakshak.mobile.data.ComplaintCategory
import com.aaharrakshak.mobile.data.ComplaintDraftRequest
import com.aaharrakshak.mobile.data.ComplaintType
import com.aaharrakshak.mobile.data.GpsLocationDto
import com.aaharrakshak.mobile.data.MobileFormValidator
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class Phase8ValidationTest {
    @Test
    fun validFssaiLicenceRequiresFourteenDigits() {
        assertTrue(MobileFormValidator.validateFssaiLicence("12345678901234"))
        assertFalse(MobileFormValidator.validateFssaiLicence("1234567890123"))
        assertFalse(MobileFormValidator.validateFssaiLicence("FSSAI123456789"))
    }

    @Test
    fun gpsLocationRequiresConsentBeforeSubmission() {
        val request = ComplaintDraftRequest(
            complaintType = ComplaintType.PACKAGED_FOOD,
            category = ComplaintCategory.SUSPECTED_ADULTERATION,
            scannedBarcode = "8901234567890",
            confirmedProductName = "Demo Turmeric Powder",
            confirmedFssaiLicenceNumber = "12345678901234",
            description = "Citizen-confirmed complaint details.",
            location = GpsLocationDto(
                consentAccepted = false,
                latitude = 18.52043,
                longitude = 73.85674,
                address = "Pune"
            )
        )

        assertFalse(MobileFormValidator.complaintCanSubmit(request))
    }

    @Test
    fun imageDisclaimerDoesNotClaimChemicalProof() {
        val disclaimer = MobileFormValidator.chemicalAdulterationDisclaimer()
        assertTrue(disclaimer.contains("laboratory confirmation", ignoreCase = true))
        assertFalse(disclaimer.contains("image proves", ignoreCase = true))
    }
}
