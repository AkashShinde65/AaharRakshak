package com.aaharrakshak.intelligence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
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
class Phase7IntelligenceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void hotspotRiskSlaAlertsAndTrustScoreRespectPrivacyAndAuthorization() throws Exception {
        String citizenToken = login("citizen@aaharrakshak.dev", "password");
        String districtToken = login("district@aaharrakshak.dev", "password");
        String inspectorToken = login("inspector@aaharrakshak.dev", "password");

        mockMvc.perform(get("/api/v1/official/intelligence/hotspots/district")
                        .header("Authorization", "Bearer " + citizenToken)
                        .param("district", "Pune"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/v1/official/intelligence/hotspots/district")
                        .header("Authorization", "Bearer " + districtToken)
                        .param("district", "Pune"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].riskLevel", hasItem("CRITICAL")))
                .andExpect(jsonPath("$[*].complaintCount", hasItem(greaterThanOrEqualTo(10))))
                .andExpect(jsonPath("$[0].privacyNote").value("Map coordinates are district-level hotspot centers. Individual citizen locations are not exposed publicly."));

        MvcResult publicSearch = mockMvc.perform(get("/api/v1/public/transparency/search")
                        .param("product", "turmeric"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ticketNumber").exists())
                .andReturn();
        assertThat(publicSearch.getResponse().getContentAsString().toLowerCase())
                .doesNotContain("latitude")
                .doesNotContain("longitude")
                .doesNotContain("citizen@");

        mockMvc.perform(post("/api/v1/official/intelligence/risk/complaints/ARK-HOT-0001")
                        .header("Authorization", "Bearer " + districtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.riskLevel").value("CRITICAL"))
                .andExpect(jsonPath("$.reasons[*]", hasItem("Critical pattern: at least 10 related complaints in the configured area/time window")))
                .andExpect(jsonPath("$.imageSafetyNote", org.hamcrest.Matchers.containsString("do not prove adulteration")));

        mockMvc.perform(post("/api/v1/official/intelligence/complaints/ARK-SLA-0007/close")
                        .header("Authorization", "Bearer " + inspectorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("reason", "Silent close attempt"))))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/v1/official/intelligence/sla/check-overdue")
                        .header("Authorization", "Bearer " + districtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].ticketNumber", hasItem("ARK-SLA-0007")));

        mockMvc.perform(get("/api/v1/official/intelligence/sla/escalations")
                        .header("Authorization", "Bearer " + districtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].previousStatus", hasItem("ASSIGNED")));

        mockMvc.perform(get("/api/v1/official/intelligence/alerts/outbox")
                        .header("Authorization", "Bearer " + districtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].eventType", hasItem("SLA_ESCALATION")));

        mockMvc.perform(post("/api/v1/citizen/trust/reviews")
                        .header("Authorization", "Bearer " + citizenToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "companyId", 1,
                                "productId", 1,
                                "batchId", 1,
                                "rating", 4,
                                "reviewText", "Missing receipt should fail"))))
                .andExpect(status().isBadRequest());

        Map<String, Object> review = reviewRequest();
        mockMvc.perform(post("/api/v1/citizen/trust/reviews")
                        .header("Authorization", "Bearer " + citizenToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(review)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.receiptVerified").value(true))
                .andExpect(jsonPath("$.receiptVerificationToken").value(org.hamcrest.Matchers.startsWith("mock-receipt-")));

        mockMvc.perform(post("/api/v1/citizen/trust/reviews")
                        .header("Authorization", "Bearer " + citizenToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(review)))
                .andExpect(status().isConflict());

        mockMvc.perform(get("/api/v1/public/trust/companies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rawComplaintFairnessNote").value("Raw complaints do not directly prove guilt and are not used alone to reduce Trust Score."))
                .andExpect(jsonPath("$.explanation", org.hamcrest.Matchers.containsString("receipt-backed reviews")));

        mockMvc.perform(get("/api/v1/citizen/alerts")
                        .header("Authorization", "Bearer " + citizenToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].eventType", not(hasItem("SLA_ESCALATION"))));
    }

    private Map<String, Object> reviewRequest() {
        String checksum = UUID.randomUUID().toString().replace("-", "")
                + UUID.randomUUID().toString().replace("-", "");
        checksum = checksum.substring(0, 64).toLowerCase();
        return Map.of(
                "companyId", 1,
                "productId", 1,
                "batchId", 1,
                "rating", 4,
                "reviewText", "Receipt-backed review for Phase 7 Trust Score.",
                "receipt", Map.of(
                        "objectKey", "receipts/phase7/" + checksum + ".jpg",
                        "originalFileName", "phase7-receipt.jpg",
                        "contentType", "image/jpeg",
                        "sizeBytes", 2048,
                        "checksumSha256", checksum));
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
