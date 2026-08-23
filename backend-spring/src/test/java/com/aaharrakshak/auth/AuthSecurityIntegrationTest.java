package com.aaharrakshak.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.aaharrakshak.audit.AuditLogRepository;
import com.fasterxml.jackson.databind.JsonNode;
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
class AuthSecurityIntegrationTest {

    private static final String PASSWORD = "password";
    private static final int CONFIGURED_MAX_LOGIN_ATTEMPTS = 5;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Test
    void seededRolesCanAccessOnlyAuthorizedEndpoints() throws Exception {
        String citizenToken = login("citizen@aaharrakshak.dev", PASSWORD);
        authorizedGet(citizenToken, "/api/v1/citizen/profile")
                .andExpect(jsonPath("$.roles", hasItem("CITIZEN")));
        forbiddenGet(citizenToken, "/api/v1/admin/dashboard");

        String companyToken = login("company@aaharrakshak.dev", PASSWORD);
        authorizedGet(companyToken, "/api/v1/company/account")
                .andExpect(jsonPath("$.roles", hasItem("COMPANY")));
        forbiddenGet(companyToken, "/api/v1/citizen/profile");

        String inspectorToken = login("inspector@aaharrakshak.dev", PASSWORD);
        authorizedGet(inspectorToken, "/api/v1/official/inspectors/dashboard")
                .andExpect(jsonPath("$.role").value("FOOD_INSPECTOR"));
        forbiddenGet(inspectorToken, "/api/v1/official/lab/dashboard");

        String labToken = login("lab@aaharrakshak.dev", PASSWORD);
        authorizedGet(labToken, "/api/v1/official/lab/dashboard")
                .andExpect(jsonPath("$.role").value("LABORATORY_OFFICER"));
        forbiddenGet(labToken, "/api/v1/official/district/dashboard");

        String districtToken = login("district@aaharrakshak.dev", PASSWORD);
        authorizedGet(districtToken, "/api/v1/official/district/dashboard")
                .andExpect(jsonPath("$.role").value("DISTRICT_ESCALATION_OFFICER"));
        forbiddenGet(districtToken, "/api/v1/admin/dashboard");

        String adminToken = login("admin@aaharrakshak.dev", PASSWORD);
        authorizedGet(adminToken, "/api/v1/admin/dashboard")
                .andExpect(jsonPath("$.role").value("CENTRAL_ADMINISTRATOR"));
        forbiddenGet(adminToken, "/api/v1/official/inspectors/dashboard");
    }

    @Test
    void companyRegistrationCreatesPendingCompanyAndAuditLog() throws Exception {
        String suffix = uniqueSuffix();
        long registrationsBefore = auditLogRepository.countByAction("COMPANY_REGISTERED");

        mockMvc.perform(post("/api/v1/auth/register/company")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "contactFullName", "Demo Company Owner",
                                "email", "company-" + suffix + "@example.test",
                                "mobileNumber", "91" + suffix.substring(0, 8),
                                "password", "password123",
                                "legalName", "Phase Two Foods " + suffix,
                                "tradeName", "P2 Foods",
                                "gstin", "27P2TEST" + suffix.substring(0, 8)))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userStatus").value("ACTIVE"))
                .andExpect(jsonPath("$.companyStatus").value("PENDING_VERIFICATION"))
                .andExpect(jsonPath("$.roles", hasItem("COMPANY")));

        assertThat(auditLogRepository.countByAction("COMPANY_REGISTERED"))
                .isEqualTo(registrationsBefore + 1);
    }

    @Test
    void citizenRegistrationOtpActivationAndMockAadhaarVerification() throws Exception {
        String suffix = uniqueSuffix();
        String email = "citizen-" + suffix + "@example.test";
        String mobile = "92" + suffix.substring(0, 8);
        long registrationsBefore = auditLogRepository.countByAction("CITIZEN_REGISTERED");
        long aadhaarBefore = auditLogRepository.countByAction("MOCK_AADHAAR_VERIFIED");

        mockMvc.perform(post("/api/v1/auth/register/citizen")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "fullName", "Phase Two Citizen",
                                "email", email,
                                "mobileNumber", mobile,
                                "password", "password123"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userStatus").value("PENDING_VERIFICATION"))
                .andExpect(jsonPath("$.roles", hasItem("CITIZEN")));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("identifier", email, "password", "password123"))))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/v1/auth/otp/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("identifier", email, "channel", "EMAIL"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mockCode").value("123456"));

        mockMvc.perform(post("/api/v1/auth/otp/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("identifier", email, "channel", "EMAIL", "code", "123456"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("VERIFIED"));

        String token = login(email, "password123");

        authorizedPost(token, "/api/v1/auth/mock-aadhaar/verify", Map.of("consentAccepted", true))
                .andExpect(jsonPath("$.status").value("MOCK_AADHAAR_VERIFIED"))
                .andExpect(jsonPath("$.verificationToken", startsWith("mock-aadhaar-")));

        authorizedGet(token, "/api/v1/auth/me")
                .andExpect(jsonPath("$.identityVerificationStatus").value("MOCK_AADHAAR_VERIFIED"));

        assertThat(auditLogRepository.countByAction("CITIZEN_REGISTERED"))
                .isEqualTo(registrationsBefore + 1);
        assertThat(auditLogRepository.countByAction("MOCK_AADHAAR_VERIFIED"))
                .isEqualTo(aadhaarBefore + 1);
    }

    @Test
    void repeatedFailedLoginAttemptsLockAccountTemporarily() throws Exception {
        String suffix = uniqueSuffix();
        String email = "lockout-" + suffix + "@example.test";
        registerAndVerifyCitizen(email, "93" + suffix.substring(0, 8));

        for (int attempt = 0; attempt < CONFIGURED_MAX_LOGIN_ATTEMPTS; attempt++) {
            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(Map.of("identifier", email, "password", "wrong-password"))))
                    .andExpect(status().isUnauthorized());
        }

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("identifier", email, "password", "password123"))))
                .andExpect(status().isLocked());
    }

    private void registerAndVerifyCitizen(String email, String mobile) throws Exception {
        mockMvc.perform(post("/api/v1/auth/register/citizen")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "fullName", "Lockout Citizen",
                                "email", email,
                                "mobileNumber", mobile,
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
    }

    private String login(String identifier, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("identifier", identifier, "password", password))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andReturn();
        JsonNode response = objectMapper.readTree(result.getResponse().getContentAsString());
        return response.path("accessToken").asText();
    }

    private org.springframework.test.web.servlet.ResultActions authorizedGet(String token, String path) throws Exception {
        return mockMvc.perform(get(path)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    private void forbiddenGet(String token, String path) throws Exception {
        mockMvc.perform(get(path)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    private org.springframework.test.web.servlet.ResultActions authorizedPost(
            String token,
            String path,
            Map<String, Object> body) throws Exception {
        return mockMvc.perform(post(path)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(body)))
                .andExpect(status().isOk());
    }

    private String json(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }

    private String uniqueSuffix() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }
}
