package com.aaharrakshak.complaint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CitizenComplaintIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void packagedFoodScanDraftEvidenceSubmitAndCitizenOwnershipFlow() throws Exception {
        String citizenToken = login("citizen@aaharrakshak.dev", "password");

        mockMvc.perform(post("/api/v1/citizen/scans/packaged-food")
                        .header("Authorization", "Bearer " + citizenToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "barcode", "8901234567890",
                                "frontLabelImage", file("scan/front.jpg", "front.jpg", "image/jpeg", 1000)))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.barcodeMatched").value(true))
                .andExpect(jsonPath("$.matchedProduct.productName").value("Demo Turmeric Powder"))
                .andExpect(jsonPath("$.ocrDetails.productName").value("Demo Turmeric Powder"))
                .andExpect(jsonPath("$.safetyNote", containsString("do not prove adulteration")));

        MvcResult draftResult = mockMvc.perform(post("/api/v1/citizen/complaints/drafts")
                        .header("Authorization", "Bearer " + citizenToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(packagedDraftRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ticketNumber", startsWith("ARK-")))
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andExpect(jsonPath("$.productName").value("Demo Turmeric Powder"))
                .andExpect(jsonPath("$.evidence[0].checksumSha256").value("b".repeat(64)))
                .andExpect(jsonPath("$.statusHistory[0].status").value("DRAFT"))
                .andReturn();
        JsonNode draft = objectMapper.readTree(draftResult.getResponse().getContentAsString());
        long complaintId = draft.path("complaintId").asLong();
        String ticketNumber = draft.path("ticketNumber").asText();

        mockMvc.perform(post("/api/v1/citizen/complaints/" + complaintId + "/evidence")
                        .header("Authorization", "Bearer " + citizenToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(evidence(EvidenceType.RECEIPT_FILE, "complaints/receipt.pdf", "application/pdf", "c".repeat(64)))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.evidence[*].type", hasItem("RECEIPT_FILE")));

        mockMvc.perform(post("/api/v1/citizen/complaints/" + complaintId + "/submit")
                        .header("Authorization", "Bearer " + citizenToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUBMITTED"))
                .andExpect(jsonPath("$.statusHistory[*].status", hasItem("SUBMITTED")));

        mockMvc.perform(get("/api/v1/citizen/complaints/" + ticketNumber)
                        .header("Authorization", "Bearer " + citizenToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticketNumber").value(ticketNumber));

        String otherCitizenToken = registerVerifiedCitizenAndLogin();
        mockMvc.perform(get("/api/v1/citizen/complaints/" + ticketNumber)
                        .header("Authorization", "Bearer " + otherCitizenToken))
                .andExpect(status().isNotFound());

        String companyToken = login("company@aaharrakshak.dev", "password");
        mockMvc.perform(get("/api/v1/citizen/complaints")
                        .header("Authorization", "Bearer " + companyToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void preparedDishComplaintAllowsUnknownVendorAndRequiresDishVendorImagesAndGps() throws Exception {
        String citizenToken = login("citizen@aaharrakshak.dev", "password");

        MvcResult draftResult = mockMvc.perform(post("/api/v1/citizen/complaints/drafts")
                        .header("Authorization", "Bearer " + citizenToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(preparedDishDraftRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.complaintType").value("PREPARED_DISH"))
                .andExpect(jsonPath("$.companyId").doesNotExist())
                .andExpect(jsonPath("$.vendorName").value("Unknown Chaat Vendor"))
                .andReturn();
        long complaintId = objectMapper.readTree(draftResult.getResponse().getContentAsString())
                .path("complaintId")
                .asLong();

        mockMvc.perform(post("/api/v1/citizen/complaints/" + complaintId + "/submit")
                        .header("Authorization", "Bearer " + citizenToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUBMITTED"))
                .andExpect(jsonPath("$.companyId").doesNotExist());
    }

    @Test
    void invalidEvidenceAndIncompletePreparedDishSubmissionAreRejected() throws Exception {
        String citizenToken = login("citizen@aaharrakshak.dev", "password");

        MvcResult draftResult = mockMvc.perform(post("/api/v1/citizen/complaints/drafts")
                        .header("Authorization", "Bearer " + citizenToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "complaintType", "PREPARED_DISH",
                                "category", "HYGIENE_ISSUE",
                                "vendorName", "Unknown Vendor",
                                "location", location(),
                                "evidence", List.of(evidence(EvidenceType.DISH_IMAGE, "complaints/dish.jpg", "image/jpeg", "d".repeat(64)))))))
                .andExpect(status().isCreated())
                .andReturn();
        long complaintId = objectMapper.readTree(draftResult.getResponse().getContentAsString())
                .path("complaintId")
                .asLong();

        mockMvc.perform(post("/api/v1/citizen/complaints/" + complaintId + "/submit")
                        .header("Authorization", "Bearer " + citizenToken))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/v1/citizen/complaints/" + complaintId + "/evidence")
                        .header("Authorization", "Bearer " + citizenToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(evidence(EvidenceType.VIDEO, "complaints/bad-video.exe", "application/octet-stream", "e".repeat(64)))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void assignedOfficialsCanViewAssignedComplaintWithoutCitizenContactDetails() throws Exception {
        String inspectorToken = login("inspector@aaharrakshak.dev", "password");

        mockMvc.perform(get("/api/v1/official/complaints/assigned")
                        .header("Authorization", "Bearer " + inspectorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].ticketNumber", hasItem("ARK-SEED-0001")));

        MvcResult officialResult = mockMvc.perform(get("/api/v1/official/complaints/ARK-SEED-0001")
                        .header("Authorization", "Bearer " + inspectorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticketNumber").value("ARK-SEED-0001"))
                .andReturn();
        String body = officialResult.getResponse().getContentAsString();
        assertThat(body).doesNotContain("citizen@aaharrakshak.dev");
        assertThat(body).doesNotContain("9000000001");

        String labToken = login("lab@aaharrakshak.dev", "password");
        mockMvc.perform(get("/api/v1/official/complaints/ARK-SEED-0001")
                        .header("Authorization", "Bearer " + labToken))
                .andExpect(status().isForbidden());
    }

    private Map<String, Object> packagedDraftRequest() {
        return Map.ofEntries(
                Map.entry("complaintType", "PACKAGED_FOOD"),
                Map.entry("category", "SUSPECTED_ADULTERATION"),
                Map.entry("scannedBarcode", "8901234567890"),
                Map.entry("productId", 1),
                Map.entry("detectedProductName", "Demo Turmeric Powder"),
                Map.entry("detectedCompanyName", "Demo Foods Private Limited"),
                Map.entry("detectedFssaiLicenceNumber", "12345678901234"),
                Map.entry("detectedBatchNumber", "TUR-2026-001"),
                Map.entry("detectedExpiryDate", "2027-01-14"),
                Map.entry("confirmedProductName", "Demo Turmeric Powder"),
                Map.entry("confirmedCompanyName", "Demo Foods Private Limited"),
                Map.entry("confirmedFssaiLicenceNumber", "12345678901234"),
                Map.entry("confirmedBatchNumber", "TUR-2026-001"),
                Map.entry("confirmedExpiryDate", "2027-01-14"),
                Map.entry("description", "User corrected OCR fields and submitted label evidence."),
                Map.entry("location", location()),
                Map.entry("evidence", List.of(evidence(
                        EvidenceType.PRODUCT_LABEL_PHOTO,
                        "complaints/product-label.jpg",
                        "image/jpeg",
                        "b".repeat(64)))));
    }

    private Map<String, Object> preparedDishDraftRequest() {
        return Map.ofEntries(
                Map.entry("complaintType", "PREPARED_DISH"),
                Map.entry("category", "HYGIENE_ISSUE"),
                Map.entry("vendorName", "Unknown Chaat Vendor"),
                Map.entry("vendorAddress", "Near demo bus stand"),
                Map.entry("description", "Prepared dish complaint with vendor and dish images. Image is not treated as proof."),
                Map.entry("location", location()),
                Map.entry("evidence", List.of(
                        evidence(EvidenceType.DISH_IMAGE, "complaints/dish.jpg", "image/jpeg", "f".repeat(64)),
                        evidence(EvidenceType.VENDOR_IMAGE, "complaints/vendor.jpg", "image/jpeg", "1".repeat(64)))));
    }

    private Map<String, Object> location() {
        return Map.of(
                "consentAccepted", true,
                "latitude", 18.52043,
                "longitude", 73.85674,
                "address", "Pune demo market");
    }

    private Map<String, Object> evidence(EvidenceType type, String objectKey, String contentType, String checksum) {
        return Map.of(
                "type", type.name(),
                "objectKey", objectKey,
                "originalFileName", objectKey.substring(objectKey.lastIndexOf('/') + 1),
                "contentType", contentType,
                "sizeBytes", 1024,
                "checksumSha256", checksum,
                "capturedAt", "2026-01-01T10:00:00Z");
    }

    private Map<String, Object> file(String objectKey, String fileName, String contentType, long sizeBytes) {
        return Map.of(
                "objectKey", objectKey,
                "originalFileName", fileName,
                "contentType", contentType,
                "sizeBytes", sizeBytes);
    }

    private String registerVerifiedCitizenAndLogin() throws Exception {
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        String email = "phase4-citizen-" + suffix + "@example.test";
        mockMvc.perform(post("/api/v1/auth/register/citizen")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "fullName", "Phase 4 Citizen",
                                "email", email,
                                "mobileNumber", "95" + suffix.substring(0, 8),
                                "password", "password123"))))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/v1/auth/otp/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("identifier", email, "channel", "EMAIL"))))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/v1/auth/otp/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("identifier", email, "channel", "EMAIL", "code", "123456"))))
                .andExpect(status().isOk());
        return login(email, "password123");
    }

    private String login(String identifier, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("identifier", identifier, "password", password))))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode response = objectMapper.readTree(result.getResponse().getContentAsString());
        return response.path("accessToken").asText();
    }

    private String json(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }
}
