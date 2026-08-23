package com.aaharrakshak.action;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
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
class AdministrativeActionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void noticeResponseDecisionAndPublicTransparencyAreRedacted() throws Exception {
        String districtToken = login("district@aaharrakshak.dev", "password");
        String companyToken = login("company@aaharrakshak.dev", "password");
        String labToken = login("lab@aaharrakshak.dev", "password");
        String inspectorToken = login("inspector@aaharrakshak.dev", "password");

        long reportId = phaseSixReportId(districtToken);

        mockMvc.perform(post("/api/v1/official/admin-actions/reports/" + reportId + "/show-cause-notices")
                        .header("Authorization", "Bearer " + labToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(issueNoticeRequest())))
                .andExpect(status().isForbidden());

        MvcResult noticeResult = mockMvc.perform(post("/api/v1/official/admin-actions/reports/" + reportId + "/show-cause-notices")
                        .header("Authorization", "Bearer " + districtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(issueNoticeRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticketNumber").value("ARK-SEED-0006"))
                .andExpect(jsonPath("$.outcome").value("ADULTERATED"))
                .andExpect(jsonPath("$.status").value("ISSUED"))
                .andReturn();
        String noticeNumber = objectMapper.readTree(noticeResult.getResponse().getContentAsString())
                .path("noticeNumber")
                .asText();

        mockMvc.perform(get("/api/v1/company/admin-actions/notices")
                        .header("Authorization", "Bearer " + companyToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].noticeNumber", hasItem(noticeNumber)));

        mockMvc.perform(post("/api/v1/company/admin-actions/notices/" + noticeNumber + "/responses")
                        .header("Authorization", "Bearer " + companyToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(companyResponseRequest(noticeNumber))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RESPONDED"))
                .andExpect(jsonPath("$.responses[0].document.checksumSha256").value("f".repeat(64)));

        mockMvc.perform(post("/api/v1/official/admin-actions/notices/" + noticeNumber + "/review")
                        .header("Authorization", "Bearer " + districtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("notes", "Response reviewed for simulated decision"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UNDER_REVIEW"));

        mockMvc.perform(post("/api/v1/official/admin-actions/notices/" + noticeNumber + "/decision")
                        .header("Authorization", "Bearer " + inspectorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(decisionRequest())))
                .andExpect(status().isForbidden());

        MvcResult decisionResult = mockMvc.perform(post("/api/v1/official/admin-actions/notices/" + noticeNumber + "/decision")
                        .header("Authorization", "Bearer " + districtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(decisionRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.actionType").value("BATCH_RECALL"))
                .andExpect(jsonPath("$.simulated").value(true))
                .andReturn();
        String actionNumber = objectMapper.readTree(decisionResult.getResponse().getContentAsString())
                .path("actionNumber")
                .asText();

        mockMvc.perform(get("/api/v1/public/transparency/complaints/ARK-SEED-0006/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTION_TAKEN"))
                .andExpect(jsonPath("$.publishedReports[0].outcome").value("ADULTERATED"));

        MvcResult publicReport = mockMvc.perform(get("/api/v1/public/transparency/reports/LAB-SEED-0006"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.action.actionNumber").value(actionNumber))
                .andExpect(jsonPath("$.privacyNotice").exists())
                .andReturn();
        String publicBody = publicReport.getResponse().getContentAsString().toLowerCase();
        assertThat(publicBody)
                .doesNotContain("citizen@aaharrakshak.dev")
                .doesNotContain("9000000001")
                .doesNotContain("submittedby")
                .doesNotContain("seal-seed")
                .doesNotContain("chainofcustody");

        mockMvc.perform(get("/api/v1/public/transparency/search")
                        .param("product", "turmeric"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].ticketNumber", hasItem("ARK-SEED-0006")));

        mockMvc.perform(get("/api/v1/public/transparency/licences/12345678901234/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.safetyNote").value("Simulated administrative statuses are demo records and do not perform real government action."));

        mockMvc.perform(get("/api/v1/public/transparency/batches/TUR-2026-001/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.platformStatus").value("RECALLED"));

        mockMvc.perform(get("/api/v1/public/transparency/recalls"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].actionNumber", hasItem(actionNumber)));

        mockMvc.perform(get("/api/v1/public/transparency/alerts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].title", hasItem("Batch recall notice")));

        mockMvc.perform(get("/api/v1/official/intelligence/alerts/outbox")
                        .header("Authorization", "Bearer " + districtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].eventType", hasItem("LOCATION_BATCH_RECALL")));

        mockMvc.perform(get("/api/v1/official/intelligence/mock-external-events")
                        .header("Authorization", "Bearer " + districtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].eventType", hasItem("MOCK_BATCH_RECALLED")))
                .andExpect(jsonPath("$[*].safetyNote", hasItem("Mock external events never disable or modify a real storefront, delivery or payment account.")));
    }

    private long phaseSixReportId(String districtToken) throws Exception {
        MvcResult result = mockMvc.perform(get("/api/v1/official/admin-actions/dashboard")
                        .header("Authorization", "Bearer " + districtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].reportNumber", hasItem("LAB-SEED-0006")))
                .andReturn();
        JsonNode response = objectMapper.readTree(result.getResponse().getContentAsString());
        for (JsonNode item : response) {
            if ("LAB-SEED-0006".equals(item.path("reportNumber").asText())) {
                return item.path("reportId").asLong();
            }
        }
        throw new AssertionError("Seeded Phase 6 report was not returned");
    }

    private Map<String, Object> issueNoticeRequest() {
        return Map.of(
                "subject", "Show-cause notice for published laboratory report",
                "reason", "Published mock lab outcome requires company explanation before simulated action.",
                "evidenceSummary", "Reviewed lab report and complaint record. Citizen private details are redacted.",
                "responseDueAt", java.time.Instant.now().plus(java.time.Duration.ofDays(7)).toString());
    }

    private Map<String, Object> decisionRequest() {
        return Map.of(
                "actionType", "BATCH_RECALL",
                "reason", "Company response did not resolve the published adulterated mock lab outcome.",
                "evidenceSummary", "Decision is based on reviewed report and company response document metadata.",
                "effectiveDate", "2026-08-20",
                "publicSummary", "Simulated recall notice for the affected demo batch. This is not a real government action.");
    }

    private Map<String, Object> companyResponseRequest(String noticeNumber) {
        return Map.of(
                "responseText", "We reviewed the mock lab outcome and submit corrective records.",
                "document", Map.of(
                        "objectKey", "company-responses/" + noticeNumber + "/response.pdf",
                        "originalFileName", "response.pdf",
                        "contentType", "application/pdf",
                        "sizeBytes", 4096,
                        "checksumSha256", "f".repeat(64)));
    }

    private String login(String identifier, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("identifier", identifier, "password", password))))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString())
                .path("accessToken")
                .asText();
    }

    private String json(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }
}
