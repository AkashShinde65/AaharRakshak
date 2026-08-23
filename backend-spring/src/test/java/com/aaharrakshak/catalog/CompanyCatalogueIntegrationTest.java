package com.aaharrakshak.catalog;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
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
class CompanyCatalogueIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void companyManagesOnlyOwnedProductsAndBatchesAndPublicCanLookup() throws Exception {
        String companyToken = login("company@aaharrakshak.dev", "password");
        String suffix = uniqueSuffix();
        String barcode = barcode13();

        mockMvc.perform(get("/api/v1/company/profile")
                        .header("Authorization", "Bearer " + companyToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.legalName").value("Demo Foods Private Limited"));

        mockMvc.perform(put("/api/v1/company/profile")
                        .header("Authorization", "Bearer " + companyToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "legalName", "Demo Foods Private Limited",
                                "tradeName", "Demo Foods",
                                "gstin", "27ABCDE1234F1Z5",
                                "registeredAddress", "Phase 3 Test Estate",
                                "contactEmail", "company@aaharrakshak.dev",
                                "contactMobile", "9000000002",
                                "websiteUrl", "https://demo-foods.example.test"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.registeredAddress").value("Phase 3 Test Estate"));

        MvcResult productResult = mockMvc.perform(post("/api/v1/company/products")
                        .header("Authorization", "Bearer " + companyToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(productRequest("Phase 3 Turmeric " + suffix, barcode))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.primaryBarcode").value(barcode))
                .andExpect(jsonPath("$.barcodes", hasItem(barcode)))
                .andExpect(jsonPath("$.frontLabelObjectKey", startsWith("test/products/")))
                .andReturn();
        long productId = objectMapper.readTree(productResult.getResponse().getContentAsString())
                .path("productId")
                .asLong();

        MvcResult batchResult = mockMvc.perform(post("/api/v1/company/products/" + productId + "/batches")
                        .header("Authorization", "Bearer " + companyToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "batchNumber", "P3-" + suffix,
                                "manufacturedOn", "2026-02-01",
                                "expiresOn", "2027-02-01",
                                "status", "ACTIVE"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andReturn();
        long batchId = objectMapper.readTree(batchResult.getResponse().getContentAsString())
                .path("batchId")
                .asLong();

        mockMvc.perform(get("/api/v1/public/products/barcodes/" + barcode))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Phase 3 Turmeric " + suffix))
                .andExpect(jsonPath("$.barcodes", hasItem(barcode)));

        mockMvc.perform(get("/api/v1/public/products/search")
                        .param("query", "Turmeric " + suffix))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].primaryBarcode").value(barcode));

        String otherCompanyEmail = "other-company-" + suffix + "@example.test";
        registerCompany(otherCompanyEmail, "94" + suffix.substring(0, 8), "Other Foods " + suffix);
        String otherCompanyToken = login(otherCompanyEmail, "password123");

        mockMvc.perform(get("/api/v1/company/products/" + productId)
                        .header("Authorization", "Bearer " + otherCompanyToken))
                .andExpect(status().isNotFound());

        mockMvc.perform(put("/api/v1/company/batches/" + batchId)
                        .header("Authorization", "Bearer " + otherCompanyToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "batchNumber", "P3-" + suffix,
                                "manufacturedOn", "2026-02-01",
                                "expiresOn", "2027-02-01",
                                "status", "BLOCKED"))))
                .andExpect(status().isNotFound());
    }

    @Test
    void licenceWorkflowRequiresOfficialsAndSupportsVerifyRejectAndExpire() throws Exception {
        String companyToken = login("company@aaharrakshak.dev", "password");

        mockMvc.perform(post("/api/v1/company/licences")
                        .header("Authorization", "Bearer " + companyToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("licenceNumber", "123"))))
                .andExpect(status().isBadRequest());

        long verifiedLicenceId = submitLicence(companyToken, licence14("55", "1234"));

        mockMvc.perform(post("/api/v1/official/licences/" + verifiedLicenceId + "/verify")
                        .header("Authorization", "Bearer " + companyToken))
                .andExpect(status().isForbidden());

        String labToken = login("lab@aaharrakshak.dev", "password");
        mockMvc.perform(post("/api/v1/official/licences/" + verifiedLicenceId + "/verify")
                        .header("Authorization", "Bearer " + labToken))
                .andExpect(status().isForbidden());

        String inspectorToken = login("inspector@aaharrakshak.dev", "password");
        mockMvc.perform(post("/api/v1/official/licences/" + verifiedLicenceId + "/verify")
                        .header("Authorization", "Bearer " + inspectorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.registryStatus").value("VALID"))
                .andExpect(jsonPath("$.registryReferenceToken", startsWith("mock-fssai-")));

        long rejectedLicenceId = submitLicence(companyToken, licence14("66", "5678"));
        String adminToken = login("admin@aaharrakshak.dev", "password");
        mockMvc.perform(post("/api/v1/official/licences/" + rejectedLicenceId + "/reject")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("reason", "Document image does not match submitted licence details"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"))
                .andExpect(jsonPath("$.rejectionReason").value("Document image does not match submitted licence details"));

        long expiredLicenceId = submitLicence(companyToken, licence14("77", "9012"));
        String districtToken = login("district@aaharrakshak.dev", "password");
        mockMvc.perform(post("/api/v1/official/licences/" + expiredLicenceId + "/expire")
                        .header("Authorization", "Bearer " + districtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("EXPIRED"));
    }

    private Map<String, Object> productRequest(String name, String barcode) {
        return Map.of(
                "name", name,
                "brand", "Demo Gold",
                "category", "Spices",
                "manufacturerName", "Demo Foods Private Limited",
                "primaryBarcode", barcode,
                "description", "Integration-test product.",
                "frontLabelImage", Map.of(
                        "objectKey", "test/products/" + barcode + "-front.jpg",
                        "originalFileName", "front.jpg",
                        "contentType", "image/jpeg",
                        "sizeBytes", 32100),
                "licenceLabelImage", Map.of(
                        "objectKey", "test/products/" + barcode + "-licence.jpg",
                        "originalFileName", "licence.jpg",
                        "contentType", "image/jpeg",
                        "sizeBytes", 12200));
    }

    private long submitLicence(String companyToken, String licenceNumber) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/company/licences")
                        .header("Authorization", "Bearer " + companyToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "licenceNumber", licenceNumber,
                                "issuingAuthority", "Mock FSSAI Licence Registry",
                                "validFrom", "2026-01-01",
                                "validTo", "2028-01-01",
                                "licenceLabelImage", Map.of(
                                        "objectKey", "test/licences/" + licenceNumber + ".jpg",
                                        "originalFileName", licenceNumber + ".jpg",
                                        "contentType", "image/jpeg",
                                        "sizeBytes", 22000)))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING_VERIFICATION"))
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).path("licenceId").asLong();
    }

    private void registerCompany(String email, String mobileNumber, String legalName) throws Exception {
        mockMvc.perform(post("/api/v1/auth/register/company")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "contactFullName", "Other Owner",
                                "email", email,
                                "mobileNumber", mobileNumber,
                                "password", "password123",
                                "legalName", legalName,
                                "tradeName", "Other Foods",
                                "gstin", "27OTHER1234Z5"))))
                .andExpect(status().isCreated());
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

    private String uniqueSuffix() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }

    private String barcode13() {
        return String.format("89%011d", ThreadLocalRandom.current().nextLong(0, 100_000_000_000L));
    }

    private String licence14(String prefix, String ending) {
        return String.format("%s%08d%s", prefix, ThreadLocalRandom.current().nextInt(100_000_000), ending);
    }
}
