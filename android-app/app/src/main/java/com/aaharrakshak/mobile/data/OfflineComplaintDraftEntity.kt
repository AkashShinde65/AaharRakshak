package com.aaharrakshak.mobile.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "complaint_drafts")
data class OfflineComplaintDraftEntity(
    @PrimaryKey(autoGenerate = true) val localId: Long = 0,
    val serverComplaintId: Long? = null,
    val complaintType: String,
    val category: String,
    val productOrDishName: String?,
    val companyOrVendorName: String?,
    val fssaiLicenceNumber: String?,
    val batchNumber: String?,
    val expiryDate: String?,
    val barcode: String?,
    val latitude: Double?,
    val longitude: Double?,
    val address: String?,
    val gpsConsentAccepted: Boolean,
    val evidenceCount: Int,
    val description: String?,
    val syncStatus: String,
    val updatedAtEpochMs: Long
)

fun ComplaintDraftRequest.toOfflineEntity(
    localId: Long = 0,
    syncStatus: String = "LOCAL_DRAFT"
): OfflineComplaintDraftEntity = OfflineComplaintDraftEntity(
    localId = localId,
    complaintType = complaintType.name,
    category = category.name,
    productOrDishName = confirmedProductName ?: detectedProductName,
    companyOrVendorName = confirmedCompanyName ?: detectedCompanyName ?: vendorName,
    fssaiLicenceNumber = confirmedFssaiLicenceNumber ?: detectedFssaiLicenceNumber,
    batchNumber = confirmedBatchNumber ?: detectedBatchNumber,
    expiryDate = confirmedExpiryDate ?: detectedExpiryDate,
    barcode = scannedBarcode,
    latitude = location?.latitude,
    longitude = location?.longitude,
    address = location?.address,
    gpsConsentAccepted = location?.consentAccepted == true,
    evidenceCount = evidence.size,
    description = description,
    syncStatus = syncStatus,
    updatedAtEpochMs = System.currentTimeMillis()
)
