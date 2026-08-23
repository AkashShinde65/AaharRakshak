package com.aaharrakshak.mobile.network

import com.aaharrakshak.mobile.data.AlertOutboxResponse
import com.aaharrakshak.mobile.data.AuthResponse
import com.aaharrakshak.mobile.data.ComplaintDraftRequest
import com.aaharrakshak.mobile.data.ComplaintResponse
import com.aaharrakshak.mobile.data.FileMetadataDto
import com.aaharrakshak.mobile.data.HotspotResponse
import com.aaharrakshak.mobile.data.LoginRequest
import com.aaharrakshak.mobile.data.OtpRequest
import com.aaharrakshak.mobile.data.PublicBatchStatusResponse
import com.aaharrakshak.mobile.data.PublicComplaintStatusResponse
import com.aaharrakshak.mobile.data.PublicLabReportResponse
import com.aaharrakshak.mobile.data.PublicLicenceStatusResponse
import com.aaharrakshak.mobile.data.PublicProductResponse
import com.aaharrakshak.mobile.data.RegisterCitizenRequest
import com.aaharrakshak.mobile.data.RegistrationResponse
import com.aaharrakshak.mobile.data.SafetyAlertResponse
import com.aaharrakshak.mobile.data.TrustScoreResponse
import com.aaharrakshak.mobile.data.VendorReviewRequest
import com.aaharrakshak.mobile.data.VendorReviewResponse
import com.aaharrakshak.mobile.data.VerificationResponse
import com.aaharrakshak.mobile.data.VerifyOtpRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface AaharRakshakApi {
    @POST("api/v1/auth/register/citizen")
    suspend fun registerCitizen(@Body request: RegisterCitizenRequest): RegistrationResponse

    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("api/v1/auth/otp/request")
    suspend fun requestOtp(@Body request: OtpRequest): VerificationResponse

    @POST("api/v1/auth/otp/verify")
    suspend fun verifyOtp(@Body request: VerifyOtpRequest): VerificationResponse

    @GET("api/v1/public/products/barcodes/{barcode}")
    suspend fun productByBarcode(@Path("barcode") barcode: String): PublicProductResponse

    @GET("api/v1/public/products/search")
    suspend fun searchProducts(@Query("query") query: String): List<PublicProductResponse>

    @POST("api/v1/citizen/complaints/drafts")
    suspend fun createComplaintDraft(@Body request: ComplaintDraftRequest): ComplaintResponse

    @POST("api/v1/citizen/complaints/{complaintId}/evidence")
    suspend fun addEvidence(
        @Path("complaintId") complaintId: Long,
        @Body request: FileMetadataDto
    ): ComplaintResponse

    @POST("api/v1/citizen/complaints/{complaintId}/submit")
    suspend fun submitComplaint(@Path("complaintId") complaintId: Long): ComplaintResponse

    @GET("api/v1/citizen/complaints")
    suspend fun myComplaints(): List<ComplaintResponse>

    @GET("api/v1/citizen/complaints/{ticketNumber}")
    suspend fun complaint(@Path("ticketNumber") ticketNumber: String): ComplaintResponse

    @GET("api/v1/public/transparency/complaints/{ticketNumber}/status")
    suspend fun publicComplaintStatus(@Path("ticketNumber") ticketNumber: String): PublicComplaintStatusResponse

    @GET("api/v1/public/transparency/reports/{reportNumber}")
    suspend fun publicLabReport(@Path("reportNumber") reportNumber: String): PublicLabReportResponse

    @GET("api/v1/public/transparency/licences/{licenceNumber}/status")
    suspend fun publicLicenceStatus(@Path("licenceNumber") licenceNumber: String): PublicLicenceStatusResponse

    @GET("api/v1/public/transparency/batches/{batchNumber}/status")
    suspend fun publicBatchStatus(@Path("batchNumber") batchNumber: String): PublicBatchStatusResponse

    @GET("api/v1/public/transparency/alerts")
    suspend fun safetyAlerts(): List<SafetyAlertResponse>

    @GET("api/v1/citizen/alerts")
    suspend fun citizenAlerts(): List<AlertOutboxResponse>

    @GET("api/v1/public/trust/companies/{companyId}")
    suspend fun trustScore(@Path("companyId") companyId: Long): TrustScoreResponse

    @POST("api/v1/citizen/trust/reviews")
    suspend fun submitReview(@Body request: VendorReviewRequest): VendorReviewResponse

    @GET("api/v1/official/intelligence/hotspots/district")
    suspend fun officialHotspots(@Query("district") district: String? = null): List<HotspotResponse>
}
