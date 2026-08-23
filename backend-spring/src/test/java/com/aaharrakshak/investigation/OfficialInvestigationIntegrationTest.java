package com.aaharrakshak.investigation;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.aaharrakshak.complaint.EvidenceType;
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
class OfficialInvestigationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void districtAssignsComplaintAndInspectorRecordsVisitAndSample() throws Exception {
        String citizenToken = login("citizen@aaharrakshak.dev", "password");
        String ticketNumber = createSubmittedComplaint(citizenToken);
        String districtToken = login("district@aaharrakshak.dev", "password");

        mockMvc.perform(get("/api/v1/official/investigations/dashboard")
                        .header("Authorization", "Bearer " + districtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].ticketNumber", hasItem(ticketNumber)));

        mockMvc.perform(post("/api/v1/official/investigations/complaints/" + ticketNumber + "/assign-inspector")
                        .header("Authorization", "Bearer " + districtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "inspectorUserId", 3,
                                "district", "Pune",
                                "slaHours", 48,
                                "notes", "Assign to nearest inspector"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ASSIGNED"))
                .andExpect(jsonPath("$.assignedInspectorId").value(3))
                .andExpect(jsonPath("$.latitude").value(18.52043));

        String inspectorToken = login("inspector@aaharrakshak.dev", "password");
        mockMvc.perform(get("/api/v1/official/investigations/complaints/" + ticketNumber)
                        .header("Authorization", "Bearer " + inspectorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticketNumber").value(ticketNumber));

        String labToken = login("lab@aaharrakshak.dev", "password");
        mockMvc.perform(get("/api/v1/official/investigations/complaints/" + ticketNumber)
                        .header("Authorization", "Bearer " + labToken))
                .andExpect(status().isForbidden());

        MvcResult scheduled = mockMvc.perform(post("/api/v1/official/investigations/complaints/" + ticketNumber + "/inspections/schedule")
                        .header("Authorization", "Bearer " + inspectorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "scheduledAt", "2026-08-01T10:30:00Z",
                                "locationText", "Pune demo market",
                                "notes", "Visit with sealed sample kit"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SCHEDULED"))
                .andReturn();
        long inspectionId = objectMapper.readTree(scheduled.getResponse().getContentAsString())
                .path("inspectionId")
                .asLong();

        mockMvc.perform(post("/api/v1/official/investigations/inspections/" + inspectionId + "/check-in")
                        .header("Authorization", "Bearer " + inspectorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "latitude", 18.52043,
                                "longitude", 73.85674,
                                "locationText", "Pune demo market"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CHECKED_IN"));

        mockMvc.perform(post("/api/v1/official/investigations/inspections/" + inspectionId + "/visit-record")
                        .header("Authorization", "Bearer " + inspectorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "visitedAt", "2026-08-01T11:00:00Z",
                                "notes", "Inspection notes recorded. Image does not prove adulteration.",
                                "evidence", List.of(inspectionEvidence())))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.evidence[0].checksumSha256").value("d".repeat(64)));

        String sealNumber = "SEAL-P5-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        mockMvc.perform(post("/api/v1/official/investigations/inspections/" + inspectionId + "/samples")
                        .header("Authorization", "Bearer " + inspectorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "sealNumber", sealNumber,
                                "quantity", "250 g",
                                "collectedAt", "2026-08-01T11:15:00Z",
                                "latitude", 18.52043,
                                "longitude", 73.85674,
                                "locationText", "Pune demo market",
                                "storageDetails", "Sterile container, cold-box slot A2",
                                "chainOfCustodyNotes", "Collected and sealed by assigned inspector"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sampleNumber", startsWith("SMP-")))
                .andExpect(jsonPath("$.sealNumber").value(sealNumber))
                .andExpect(jsonPath("$.chainOfCustody[*].eventType", hasItem("COLLECTED")));

        mockMvc.perform(get("/api/v1/official/investigations/complaints/" + ticketNumber)
                        .header("Authorization", "Bearer " + inspectorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SAMPLE_COLLECTED"))
                .andExpect(jsonPath("$.samples[0].sealNumber").value(sealNumber));
    }

    @Test
    void labOfficerReceivesSampleSubmitsReportAndSeniorOfficialPublishes() throws Exception {
        String labToken = login("lab@aaharrakshak.dev", "password");
        long sampleId = seedLabSampleId(labToken);

        mockMvc.perform(post("/api/v1/lab/investigations/samples/" + sampleId + "/received")
                        .header("Authorization", "Bearer " + labToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "locationText", "Demo State Food Lab",
                                "storageCondition", "Cold storage rack C1, seal intact",
                                "notes", "Received with matching seal number"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RECEIVED"));

        String reportNumber = "LAB-P5-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        MvcResult draft = mockMvc.perform(post("/api/v1/lab/investigations/samples/" + sampleId + "/reports/drafts")
                        .header("Authorization", "Bearer " + labToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(reportRequest(reportNumber))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andExpect(jsonPath("$.results[0].parameterName").value("Moisture"))
                .andReturn();
        long reportId = objectMapper.readTree(draft.getResponse().getContentAsString())
                .path("reportId")
                .asLong();

        String inspectorToken = login("inspector@aaharrakshak.dev", "password");
        mockMvc.perform(post("/api/v1/lab/investigations/reports/" + reportId + "/submit")
                        .header("Authorization", "Bearer " + inspectorToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/v1/lab/investigations/reports/" + reportId + "/submit")
                        .header("Authorization", "Bearer " + labToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUBMITTED"));

        mockMvc.perform(post("/api/v1/official/investigations/lab-reports/" + reportId + "/publish")
                        .header("Authorization", "Bearer " + labToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("notes", "Lab officers cannot publish"))))
                .andExpect(status().isForbidden());

        String districtToken = login("district@aaharrakshak.dev", "password");
        mockMvc.perform(post("/api/v1/official/investigations/lab-reports/" + reportId + "/review")
                        .header("Authorization", "Bearer " + districtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("notes", "Reviewed for academic demo"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REVIEWED"));

        mockMvc.perform(post("/api/v1/official/investigations/lab-reports/" + reportId + "/publish")
                        .header("Authorization", "Bearer " + districtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("notes", "Publish public-safe report only"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PUBLISHED"));

        mockMvc.perform(get("/api/v1/official/investigations/complaints/ARK-SEED-0005")
                        .header("Authorization", "Bearer " + districtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REPORT_PUBLISHED"))
                .andExpect(jsonPath("$.samples[0].chainOfCustody[*].eventType", hasItem("REPORT_PUBLISHED")));
    }

    private long seedLabSampleId(String labToken) throws Exception {
        MvcResult result = mockMvc.perform(get("/api/v1/lab/investigations/samples/assigned")
                        .header("Authorization", "Bearer " + labToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].sampleNumber", hasItem("SMP-SEED-0005")))
                .andReturn();
        JsonNode response = objectMapper.readTree(result.getResponse().getContentAsString());
        for (JsonNode assignment : response) {
            if ("SMP-SEED-0005".equals(assignment.path("sampleNumber").asText())) {
                return assignment.path("sampleId").asLong();
            }
        }
        throw new AssertionError("Seed sample was not returned");
    }

    private String createSubmittedComplaint(String citizenToken) throws Exception {
        MvcResult draftResult = mockMvc.perform(post("/api/v1/citizen/complaints/drafts")
                        .header("Authorization", "Bearer " + citizenToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.ofEntries(
                                Map.entry("complaintType", "PACKAGED_FOOD"),
                                Map.entry("category", "SUSPECTED_ADULTERATION"),
                                Map.entry("scannedBarcode", "8901234567890"),
                                Map.entry("productId", 1),
                                Map.entry("confirmedProductName", "Demo Turmeric Powder"),
                                Map.entry("confirmedCompanyName", "Demo Foods Private Limited"),
                                Map.entry("confirmedFssaiLicenceNumber", "12345678901234"),
                                Map.entry("confirmedBatchNumber", "TUR-2026-001"),
                                Map.entry("confirmedExpiryDate", "2027-01-14"),
                                Map.entry("description", "Phase 5 submitted complaint for assignment testing."),
                                Map.entry("location", location()),
                                Map.entry("evidence", List.of(citizenEvidence()))))))
                .andExpect(status().isCreated())
                .andReturn();
        JsonNode draft = objectMapper.readTree(draftResult.getResponse().getContentAsString());
        long complaintId = draft.path("complaintId").asLong();
        String ticketNumber = draft.path("ticketNumber").asText();
        mockMvc.perform(post("/api/v1/citizen/complaints/" + complaintId + "/submit")
                        .header("Authorization", "Bearer " + citizenToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUBMITTED"));
        return ticketNumber;
    }

    private Map<String, Object> location() {
        return Map.of(
                "consentAccepted", true,
                "latitude", 18.52043,
                "longitude", 73.85674,
                "address", "Pune demo market");
    }

    private Map<String, Object> citizenEvidence() {
        return Map.of(
                "type", EvidenceType.PRODUCT_LABEL_PHOTO.name(),
                "objectKey", "complaints/phase5-product-label.jpg",
                "originalFileName", "phase5-product-label.jpg",
                "contentType", "image/jpeg",
                "sizeBytes", 2048,
                "checksumSha256", "b".repeat(64),
                "capturedAt", "2026-01-01T10:00:00Z");
    }

    private Map<String, Object> inspectionEvidence() {
        return Map.of(
                "type", EvidenceType.FOOD_PHOTO.name(),
                "objectKey", "inspection/phase5-food.jpg",
                "originalFileName", "phase5-food.jpg",
                "contentType", "image/jpeg",
                "sizeBytes", 2048,
                "checksumSha256", "d".repeat(64),
                "capturedAt", "2026-01-01T10:30:00Z");
    }

    private Map<String, Object> reportRequest(String reportNumber) {
        return Map.of(
                "reportNumber", reportNumber,
                "objectKey", "lab-reports/" + reportNumber + ".pdf",
                "originalFileName", reportNumber + ".pdf",
                "contentType", "application/pdf",
                "sizeBytes", 4096,
                "checksumSha256", "e".repeat(64),
                "resultSummary", "Parameters within demo limits",
                "results", List.of(Map.of(
                        "parameterName", "Moisture",
                        "testMethod", "Mock IS method",
                        "permissibleLimit", "< 12%",
                        "resultValue", "8.2",
                        "unit", "%",
                        "compliant", true,
                        "remarks", "Mock academic result")));
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
