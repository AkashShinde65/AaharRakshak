# AaharRakshak Requirement Traceability

Audit date: 2026-07-23.

Legend: `PASS` means implementation exists and was verified by the listed automated test, build, migration or live audit. `NOT TESTED` means implementation exists or is scaffolded, but the exact requirement was not exercised in this audit environment. No `FAIL` result was found in reproducible local verification.

## Phase 1 - Foundation

| Requirement | Implementation file | Test file | Result |
| --- | --- | --- | --- |
| Preserve required repository layout | `README.md`, `AGENTS.md` | Final filesystem inventory | PASS |
| Create Spring Boot backend with Java 21/Maven | `backend-spring/pom.xml`, `backend-spring/src/main/java/com/aaharrakshak/AaharRakshakApplication.java` | `backend-spring/src/test/java/com/aaharrakshak/AaharRakshakApplicationTests.java` | PASS |
| Provide health endpoint | `backend-spring/src/main/java/com/aaharrakshak/health/HealthController.java` | `backend-spring/src/test/java/com/aaharrakshak/health/HealthControllerTest.java`, live `/api/v1/health` | PASS |
| Use MySQL 8 and Flyway baseline | `backend-spring/src/main/resources/db/migration/V1__initial_schema.sql` | Fresh MySQL Flyway audit | PASS |
| Docker Compose MySQL setup | `infrastructure/docker-compose.yml` | `docker compose -f infrastructure/docker-compose.yml config`, MySQL health | PASS |
| ER diagram and database relationships documented | `database/schema.md`, `docs/architecture.md` | Manual audit against V1-V7 migrations | PASS |
| Permanent project rules | `AGENTS.md` | Re-read during audit | PASS |

## Phase 2 - Authentication And Security

| Requirement | Implementation file | Test file | Result |
| --- | --- | --- | --- |
| Citizen, Company, Inspector, Lab Officer, District Officer and Admin roles | `backend-spring/src/main/java/com/aaharrakshak/user/RoleName.java`, `backend-spring/src/main/resources/db/migration/V2__authentication_and_security.sql` | `backend-spring/src/test/java/com/aaharrakshak/auth/AuthSecurityIntegrationTest.java` | PASS |
| Registration APIs | `backend-spring/src/main/java/com/aaharrakshak/auth/AuthController.java`, `backend-spring/src/main/java/com/aaharrakshak/auth/AuthService.java` | `AuthSecurityIntegrationTest.java` | PASS |
| Login API | `backend-spring/src/main/java/com/aaharrakshak/auth/AuthController.java` | `AuthSecurityIntegrationTest.java`, live workflow logins | PASS |
| Spring Security configuration | `backend-spring/src/main/java/com/aaharrakshak/security/SecurityConfig.java` | `AuthSecurityIntegrationTest.java` | PASS |
| JWT access and refresh tokens | `backend-spring/src/main/java/com/aaharrakshak/security/JwtService.java`, `backend-spring/src/main/java/com/aaharrakshak/auth/RefreshToken.java` | `AuthSecurityIntegrationTest.java` | PASS |
| BCrypt password encryption | `backend-spring/src/main/java/com/aaharrakshak/auth/AuthService.java`, `V2__authentication_and_security.sql` | `AuthSecurityIntegrationTest.java` | PASS |
| Role-based API authorization | `backend-spring/src/main/java/com/aaharrakshak/security/SecurityConfig.java` | Auth, catalogue, complaint, investigation, action and intelligence integration tests | PASS |
| Mock email/mobile OTP verification | `backend-spring/src/main/java/com/aaharrakshak/auth/OtpVerification.java`, `AuthService.java` | `AuthSecurityIntegrationTest.java` | PASS |
| Mock Aadhaar verification status/token only | `backend-spring/src/main/java/com/aaharrakshak/auth/dto/MockAadhaarVerificationRequest.java`, `AuthService.java` | `AuthSecurityIntegrationTest.java`, source audit | PASS |
| Company registration defaults to pending verification | `backend-spring/src/main/java/com/aaharrakshak/company/CompanyStatus.java`, `AuthService.java` | `AuthSecurityIntegrationTest.java` | PASS |
| Login attempt protection and account status checks | `backend-spring/src/main/java/com/aaharrakshak/auth/AuthService.java`, `backend-spring/src/main/java/com/aaharrakshak/user/UserStatus.java` | `AuthSecurityIntegrationTest.java` | PASS |
| Audit logs for registration, login and admin actions | `backend-spring/src/main/java/com/aaharrakshak/audit/AuditService.java`, `AuditLog.java` | `AuthSecurityIntegrationTest.java`, live audit-log query | PASS |
| Swagger JWT authorization | `backend-spring/src/main/java/com/aaharrakshak/config/OpenApiConfig.java` | Live `/v3/api-docs` HTTP 200 | PASS |
| Flyway V2 migration | `backend-spring/src/main/resources/db/migration/V2__authentication_and_security.sql` | Fresh MySQL Flyway audit | PASS |
| Development seed users for every role | `V2__authentication_and_security.sql` | Live login workflow for seeded users | PASS |
| Allowed and denied access tests | `backend-spring/src/test/java/com/aaharrakshak/auth/AuthSecurityIntegrationTest.java` | `mvn clean verify` | PASS |

## Phase 3 - Company, Licence, Product And Batch Management

| Requirement | Implementation file | Test file | Result |
| --- | --- | --- | --- |
| Company profile management | `backend-spring/src/main/java/com/aaharrakshak/company/CompanyPortalController.java`, `CompanyService.java` | `backend-spring/src/test/java/com/aaharrakshak/catalog/CompanyCatalogueIntegrationTest.java` | PASS |
| 14-digit FSSAI licence validation | `backend-spring/src/main/java/com/aaharrakshak/company/FssaiLicenceNumberValidator.java` | `backend-spring/src/test/java/com/aaharrakshak/company/FssaiLicenceNumberValidatorTest.java` | PASS |
| Mock government licence registry adapter | `backend-spring/src/main/java/com/aaharrakshak/company/MockLicenceRegistryAdapter.java` | `backend-spring/src/test/java/com/aaharrakshak/company/MockLicenceRegistryAdapterTest.java` | PASS |
| Licence submission workflow | `CompanyPortalController.java`, `CompanyService.java` | `CompanyCatalogueIntegrationTest.java` | PASS |
| Licence verification, rejection and expiry workflow | `backend-spring/src/main/java/com/aaharrakshak/company/OfficialLicenceController.java`, `CompanyService.java` | `CompanyCatalogueIntegrationTest.java` | PASS |
| Only authorized officials verify licence details | `OfficialLicenceController.java`, `SecurityConfig.java` | `CompanyCatalogueIntegrationTest.java` | PASS |
| Company manages only its own products and batches | `backend-spring/src/main/java/com/aaharrakshak/catalog/CatalogService.java` | `CompanyCatalogueIntegrationTest.java` | PASS |
| Product, brand, category and manufacturer details | `backend-spring/src/main/java/com/aaharrakshak/catalog/Product.java` | `CompanyCatalogueIntegrationTest.java` | PASS |
| Barcode/GTIN mapping and lookup API | `ProductBarcode.java`, `PublicProductController.java` | `CompanyCatalogueIntegrationTest.java`, live barcode lookup | PASS |
| Product front-label and licence-label image metadata | `Product.java`, `Licence.java` | `CompanyCatalogueIntegrationTest.java` | PASS |
| Batch/lot, manufacturing date and expiry date | `backend-spring/src/main/java/com/aaharrakshak/catalog/Batch.java` | `CompanyCatalogueIntegrationTest.java` | PASS |
| Batch statuses ACTIVE, UNDER_INVESTIGATION, RECALLED, BLOCKED | `backend-spring/src/main/java/com/aaharrakshak/catalog/BatchStatus.java` | `CompanyCatalogueIntegrationTest.java` | PASS |
| Public product lookup by barcode or product name | `PublicProductController.java` | `CompanyCatalogueIntegrationTest.java`, live public lookup | PASS |
| MinIO-compatible file-storage interface and mock implementation | `backend-spring/src/main/java/com/aaharrakshak/storage/FileStorageService.java`, `MockMinioFileStorageService.java` | Validator/integration tests plus MinIO health check | PASS |
| Mock companies, licences, products and batches | `V3__company_licence_product_batch_management.sql` | Fresh MySQL Flyway audit | PASS |
| Flyway V3 migration | `V3__company_licence_product_batch_management.sql` | Fresh MySQL Flyway audit | PASS |
| Swagger documentation | `OpenApiConfig.java`, controller annotations | Live `/v3/api-docs` HTTP 200 | PASS |
| Authorization, validation, unit and integration tests | `CompanyCatalogueIntegrationTest.java`, `FssaiLicenceNumberValidatorTest.java`, `MockLicenceRegistryAdapterTest.java` | `mvn clean verify` | PASS |

## Phase 4 - Citizen Complaint And Food Scanning

| Requirement | Implementation file | Test file | Result |
| --- | --- | --- | --- |
| Complaint types PACKAGED_FOOD and PREPARED_DISH | `backend-spring/src/main/java/com/aaharrakshak/complaint/ComplaintType.java` | `backend-spring/src/test/java/com/aaharrakshak/complaint/CitizenComplaintIntegrationTest.java` | PASS |
| Simple scan, confirm, evidence and submit process | `CitizenScanController.java`, `CitizenComplaintController.java`, `ComplaintService.java` | `CitizenComplaintIntegrationTest.java`, live workflow | PASS |
| Barcode lookup before manual entry | `FoodScanService.java`, `PublicProductController.java` | `CitizenComplaintIntegrationTest.java`, live scan response | PASS |
| OCR adapter interface | `backend-spring/src/main/java/com/aaharrakshak/complaint/OcrAdapter.java` | `backend-spring/src/test/java/com/aaharrakshak/complaint/MockOcrAdapterTest.java` | PASS |
| Mock/local OCR implementation | `backend-spring/src/main/java/com/aaharrakshak/complaint/MockOcrAdapter.java` | `MockOcrAdapterTest.java` | PASS |
| Allow users to correct uncertain scanned details | `CitizenComplaintController.java`, `ComplaintService.java` | `CitizenComplaintIntegrationTest.java` | PASS |
| Unknown product/vendor complaint without compulsory company | `ComplaintService.java`, `Complaint.java` | `CitizenComplaintIntegrationTest.java`, live prepared-dish complaint | PASS |
| Dish complaint with vendor image, dish image and GPS location | `CitizenComplaintController.java`, `Evidence.java`, `Complaint.java` | Live prepared-dish workflow | PASS |
| Image, video and receipt evidence metadata uploads | `Evidence.java`, `EvidenceFileValidator.java` | `backend-spring/src/test/java/com/aaharrakshak/complaint/EvidenceFileValidatorTest.java` | PASS |
| File type, size and security validation | `EvidenceFileValidator.java` | `EvidenceFileValidatorTest.java` | PASS |
| Evidence checksum and timestamp | `Evidence.java`, `ComplaintService.java` | `CitizenComplaintIntegrationTest.java`, live workflow | PASS |
| GPS latitude, longitude, address and consent | `Complaint.java`, `CitizenComplaintController.java` | `CitizenComplaintIntegrationTest.java`, Android validation test | PASS |
| Complaint draft and final submission | `ComplaintService.java` | `CitizenComplaintIntegrationTest.java`, live workflow | PASS |
| Unique complaint tracking number | `ComplaintService.java` | `CitizenComplaintIntegrationTest.java`, live ticket numbers | PASS |
| Complaint status history | `ComplaintStatusHistory.java`, `ComplaintStatusHistoryRepository.java` | `CitizenComplaintIntegrationTest.java`, live audit | PASS |
| User can view only their complaints | `CitizenComplaintController.java`, `ComplaintService.java` | `CitizenComplaintIntegrationTest.java` | PASS |
| Authorized officials can view assigned complaint details | `OfficialComplaintController.java`, `ComplaintService.java` | `CitizenComplaintIntegrationTest.java`, live 403/200 role check | PASS |
| Citizen details not exposed publicly or to companies | `TransparencyService.java`, `AdministrativeActionService.java` | `AdministrativeActionIntegrationTest.java`, live redaction checks | PASS |
| Image alone never proves adulteration | `FoodScanService.java`, Android `MobileFormValidator.kt` | `MockOcrAdapterTest.java`, `Phase8ValidationTest.kt`, live scan safety note | PASS |
| Flyway V4 migration | `V4__citizen_complaint_scanning_system.sql` | Fresh MySQL Flyway audit | PASS |
| Swagger examples and Postman requests | `postman/AaharRakshak_Phase4.postman_collection.json`, OpenAPI | `jq empty`, live `/v3/api-docs` | PASS |
| Unit, security and MySQL integration tests | `CitizenComplaintIntegrationTest.java`, `EvidenceFileValidatorTest.java`, `MockOcrAdapterTest.java` | `mvn clean verify`, fresh MySQL audit | PASS |

## Phase 5 - Investigation, Sample Collection And Lab Reporting

| Requirement | Implementation file | Test file | Result |
| --- | --- | --- | --- |
| Official complaint-priority dashboard APIs | `OfficialInvestigationController.java`, `InvestigationService.java` | `backend-spring/src/test/java/com/aaharrakshak/investigation/OfficialInvestigationIntegrationTest.java` | PASS |
| Assign complaints based on district/location | `InvestigationService.java` | `OfficialInvestigationIntegrationTest.java`, live workflow | PASS |
| Inspector views only assigned complaints | `OfficialInvestigationController.java`, `ComplaintService.java` | `OfficialInvestigationIntegrationTest.java`, live role check | PASS |
| Map-ready complaint/vendor coordinates | `Complaint.java`, dashboard DTOs in `InvestigationService.java` | `OfficialInvestigationIntegrationTest.java` | PASS |
| Inspection scheduling and visit records | `InspectionVisit.java`, `InvestigationService.java` | `OfficialInvestigationIntegrationTest.java`, live workflow | PASS |
| Geotagged inspection check-in | `InspectionVisit.java`, `OfficialInvestigationController.java` | `OfficialInvestigationIntegrationTest.java`, live workflow | PASS |
| Inspection notes, images and videos | `InspectionEvidence.java`, `InvestigationFileValidator.java` | `InvestigationFileValidatorTest.java`, live workflow | PASS |
| Unique sample/seal number | `Sample.java`, `InvestigationService.java` | `OfficialInvestigationIntegrationTest.java`, live sample | PASS |
| Sample quantity, date, location and storage details | `Sample.java` | `OfficialInvestigationIntegrationTest.java`, live workflow | PASS |
| Chain-of-custody history | `SampleChainOfCustodyEvent.java` | `OfficialInvestigationIntegrationTest.java`, live audit logs | PASS |
| Laboratory assignment and sample receipt | `SampleLabAssignment.java`, `LabInvestigationController.java` | `OfficialInvestigationIntegrationTest.java`, live workflow | PASS |
| Lab testing parameters and results | `LabTestResult.java`, `LabReport.java` | `OfficialInvestigationIntegrationTest.java`, live workflow | PASS |
| Secure PDF lab-report upload metadata | `LabReport.java`, `InvestigationFileValidator.java` | `InvestigationFileValidatorTest.java` | PASS |
| Report statuses DRAFT, SUBMITTED, REVIEWED, PUBLISHED | `LabReportStatus.java` | `OfficialInvestigationIntegrationTest.java`, live workflow | PASS |
| Authorized lab officers submit reports | `LabInvestigationController.java`, `SecurityConfig.java` | `OfficialInvestigationIntegrationTest.java` | PASS |
| Authorized senior officials approve/publish reports | `OfficialInvestigationController.java`, `SecurityConfig.java` | `OfficialInvestigationIntegrationTest.java` | PASS |
| Complaint status transition validation | `ComplaintWorkflowValidator.java` | `ComplaintWorkflowValidatorTest.java` | PASS |
| Investigation audit logs | `AuditService.java`, `InvestigationService.java` | Live audit-log query | PASS |
| Evidence/report checksums | `InspectionEvidence.java`, `LabReport.java`, validators | `InvestigationFileValidatorTest.java`, live workflow | PASS |
| SLA due date for each investigation | `Complaint.java`, `InvestigationService.java` | `OfficialInvestigationIntegrationTest.java` | PASS |
| Citizen notifications without sensitive official information | `Notification.java`, `InvestigationService.java` | `OfficialInvestigationIntegrationTest.java` | PASS |
| Flyway V5 migration | `V5__official_investigation_sample_lab_reporting.sql` | Fresh MySQL Flyway audit | PASS |
| Swagger and Postman examples | `postman/AaharRakshak_Phase5.postman_collection.json`, OpenAPI | `jq empty`, live `/v3/api-docs` | PASS |

## Phase 6 - Transparency, Administrative Action And Web Portals

| Requirement | Implementation file | Test file | Result |
| --- | --- | --- | --- |
| Administrative action workflow | `backend-spring/src/main/java/com/aaharrakshak/action/AdministrativeActionService.java` | `backend-spring/src/test/java/com/aaharrakshak/action/AdministrativeActionIntegrationTest.java`, live workflow | PASS |
| Lab outcomes SAFE, SUSPICIOUS, ADULTERATED, INCONCLUSIVE | `backend-spring/src/main/java/com/aaharrakshak/investigation/LabOutcome.java` | `AdministrativeActionIntegrationTest.java` | PASS |
| Show-cause notice to company | `ShowCauseNotice.java`, `OfficialAdministrativeActionController.java` | `AdministrativeActionIntegrationTest.java`, live workflow | PASS |
| Company response and document metadata upload | `CompanyNoticeResponse.java`, `CompanyAdministrativeActionController.java` | `AdministrativeActionIntegrationTest.java`, `AdministrativeDocumentValidatorTest.java` | PASS |
| Senior-official review and final decision | `OfficialAdministrativeActionController.java`, `AdministrativeActionService.java` | `AdministrativeActionIntegrationTest.java`, live workflow | PASS |
| Actions WARNING, BATCH_RECALL, TEMPORARY_SUSPENSION, CANCELLATION | `backend-spring/src/main/java/com/aaharrakshak/investigation/ActionType.java` | `AdministrativeActionIntegrationTest.java` | PASS |
| Reason, evidence, effective date and approving official | `backend-spring/src/main/java/com/aaharrakshak/investigation/Action.java` | `AdministrativeActionIntegrationTest.java`, live workflow | PASS |
| Only senior officials approve actions | `OfficialAdministrativeActionController.java`, `SecurityConfig.java` | `AdministrativeActionIntegrationTest.java` | PASS |
| Prevent lab officer/inspector self-approval | `AdministrativeActionService.java` | `AdministrativeActionIntegrationTest.java` | PASS |
| Full action history and audit logs | `AdministrativeActionHistory.java`, `AuditService.java` | `AdministrativeActionIntegrationTest.java`, live audit-log query | PASS |
| Simulated licence suspension/cancellation only | `AdministrativeActionService.java`, `TransparencyService.java` | `AdministrativeActionIntegrationTest.java`, source audit | PASS |
| Anonymized public lab report | `TransparencyService.java`, `PublicTransparencyController.java` | `AdministrativeActionIntegrationTest.java`, live redaction check | PASS |
| Hide citizen identity and sensitive investigation info | `TransparencyService.java` | `AdministrativeActionIntegrationTest.java`, live redaction check | PASS |
| Citizen tracks complaint and views published reports | `PublicTransparencyController.java` | `AdministrativeActionIntegrationTest.java`, live public status/report | PASS |
| Public search by complaint, company, product, batch and location | `PublicTransparencyController.java`, `TransparencyService.java` | `AdministrativeActionIntegrationTest.java`, live search | PASS |
| Public licence and batch status | `PublicTransparencyController.java` | `AdministrativeActionIntegrationTest.java`, live status endpoints | PASS |
| Recall notices and safety alerts | `SafetyAlert.java`, `TransparencyService.java` | `AdministrativeActionIntegrationTest.java`, live recall list | PASS |
| Company notices and responses | `CompanyAdministrativeActionController.java` | `AdministrativeActionIntegrationTest.java`, live workflow | PASS |
| ASP.NET portal in `official-web-dotnet` | `official-web-dotnet/official-web-dotnet.csproj`, `official-web-dotnet/Program.cs` | `dotnet build`, `dotnet test` | PASS |
| Consume Spring APIs with HttpClient | `official-web-dotnet/Services/AaharRakshakApiClient.cs` | `official-web-dotnet.Tests/Test1.cs` | PASS |
| Separate Official, Company and Public Areas | `official-web-dotnet/Areas/Official`, `official-web-dotnet/Areas/Company`, `official-web-dotnet/Areas/Public` | `dotnet build` | PASS |
| Secure login and role-based navigation | `official-web-dotnet/Controllers/AuthController.cs`, `Views/Shared/_Layout.cshtml` | `official-web-dotnet.Tests/Test1.cs`, `dotnet build` | PASS |
| Razor views, ViewModels and Bootstrap | `official-web-dotnet/Models/PortalViewModels.cs`, `Areas/*/Views`, `Views/Shared/_Layout.cshtml` | `dotnet build` | PASS |
| Anti-forgery and input validation | `official-web-dotnet/Program.cs`, `PortalViewModels.cs` | `official-web-dotnet.Tests/Test1.cs` | PASS |
| Responsive desktop/mobile design | `official-web-dotnet/wwwroot/css/site.css`, Bootstrap layout | No browser visual QA suite | NOT TESTED |
| Flyway V6 migration | `V6__transparency_administrative_action_web_portals.sql` | Fresh MySQL Flyway audit | PASS |
| Authorization and privacy-redaction tests | `AdministrativeActionIntegrationTest.java` | `mvn clean verify` | PASS |

## Phase 7 - Intelligence, Hotspots, Alerts And SLA

| Requirement | Implementation file | Test file | Result |
| --- | --- | --- | --- |
| Geo-cluster complaints by radius/time window | `HotspotService.java`, `GeoDistanceCalculator.java` | `backend-spring/src/test/java/com/aaharrakshak/intelligence/Phase7IntelligenceIntegrationTest.java` | PASS |
| Default critical rule 10 related complaints in 24 hours | `HotspotService.java`, `V7__advanced_intelligence_hotspots_alerts_sla_trust.sql` | `Phase7IntelligenceIntegrationTest.java`, live hotspot detection | PASS |
| Risk levels LOW, MEDIUM, HIGH, CRITICAL | `RiskLevel.java` | `Phase7IntelligenceIntegrationTest.java` | PASS |
| OpenStreetMap/Leaflet hotspot map | `official-web-dotnet/Areas/Official/Views/Dashboard/Hotspots.cshtml` | `dotnet build` | PASS |
| Display count, product/vendor, risk level and radius | `HotspotService.java`, portal Hotspots view | `Phase7IntelligenceIntegrationTest.java`, `dotnet build` | PASS |
| District-wise official hotspot dashboard | `OfficialIntelligenceController.java`, portal dashboard | `Phase7IntelligenceIntegrationTest.java` | PASS |
| Do not expose citizen locations publicly | `TransparencyService.java`, `HotspotService.java` | `Phase7IntelligenceIntegrationTest.java` | PASS |
| Redis Pub/Sub | `RedisAlertEventPublisher.java`, `infrastructure/docker-compose.yml` | Redis health, live workflow | PASS |
| Authenticated WebSocket notifications | `WebSocketConfig.java`, `JwtAlertHandshakeInterceptor.java`, `AlertWebSocketHandler.java` | Live WebSocket auth audit, `AlertWebSocketSessionRegistryTest.java` | PASS |
| Location-based recall and safety alerts | `RecallAlertService.java`, `AlertOutboxService.java` | `AdministrativeActionIntegrationTest.java`, live workflow | PASS |
| Notify affected users by batch/region | `RecallAlertService.java` | `AdministrativeActionIntegrationTest.java` | PASS |
| In-app, email, push and SMS adapter interfaces | `NotificationChannelAdapter.java`, `MockNotificationChannelAdapter.java` | `Phase7IntelligenceIntegrationTest.java` | PASS |
| Mock email/SMS providers | `MockNotificationChannelAdapter.java` | `Phase7IntelligenceIntegrationTest.java` | PASS |
| Outbox/retry mechanism | `AlertOutbox.java`, `AlertOutboxService.java` | `Phase7IntelligenceIntegrationTest.java`, live outbox | PASS |
| Configurable SLA with 48-hour high-risk default | `SlaEscalationService.java`, application config | `Phase7IntelligenceIntegrationTest.java` | PASS |
| Scheduled overdue-ticket checking | `SchedulingConfig.java`, `SlaEscalationService.java` | `Phase7IntelligenceIntegrationTest.java` | PASS |
| Escalate overdue cases to District Officer | `SlaEscalationService.java` | `Phase7IntelligenceIntegrationTest.java`, live SLA escalation | PASS |
| Prevent inspector hiding/closing high-risk verified case | `OfficialIntelligenceController.java`, `SlaEscalationService.java` | `Phase7IntelligenceIntegrationTest.java` | PASS |
| Escalation and administrative-lapse audits | `SlaEscalation.java`, `AuditService.java` | `Phase7IntelligenceIntegrationTest.java`, live audit-log query | PASS |
| Notify inspector and senior official | `AlertOutboxService.java` | `Phase7IntelligenceIntegrationTest.java` | PASS |
| Vendor hygiene ratings and Trust Score | `TrustScoreService.java`, `VendorTrustScore.java` | `Phase7IntelligenceIntegrationTest.java`, live Trust Score | PASS |
| Receipt upload before verified review | `VendorReview.java`, `CitizenTrustController.java` | `Phase7IntelligenceIntegrationTest.java` | PASS |
| Receipt OCR adapter with mock implementation | `ReceiptOcrAdapter.java`, `MockReceiptOcrAdapter.java` | `Phase7IntelligenceIntegrationTest.java` | PASS |
| Trust Score uses inspections, lab results, recalls, receipt-backed reviews | `TrustScoreService.java` | `Phase7IntelligenceIntegrationTest.java`, live Trust Score | PASS |
| Raw complaints do not directly prove guilt | `TrustScoreService.java`, `RiskAnalysisService.java` | `Phase7IntelligenceIntegrationTest.java` | PASS |
| Anti-spam, duplicate-review and rate-limit checks | `CitizenTrustController.java`, `RateLimitingFilter.java` | `Phase7IntelligenceIntegrationTest.java`, `RateLimitingFilterTest.java` | PASS |
| AI-assisted risk analysis | `RiskAnalysisService.java`, `MockRuleBasedRiskAnalysisAdapter.java` | `Phase7IntelligenceIntegrationTest.java` | PASS |
| Explainable risk score and reasons | `RiskAnalysis.java`, `RiskAnalysisService.java` | `Phase7IntelligenceIntegrationTest.java` | PASS |
| Images suggest category but never chemical proof | `RiskAnalysisService.java`, Android validator | `Phase7IntelligenceIntegrationTest.java`, `Phase8ValidationTest.kt` | PASS |
| Laboratory confirmation mandatory | `ComplaintWorkflowValidator.java`, `RiskAnalysisService.java` | `ComplaintWorkflowValidatorTest.java`, `Phase7IntelligenceIntegrationTest.java` | PASS |
| Mock external lockdown events | `MockExternalAccountEventPublisher.java`, `MockExternalEvent.java` | `AdministrativeActionIntegrationTest.java`, live mock event | PASS |
| Storefront, delivery and payment integration interfaces | `ExternalAccountEventPublisher.java`, `MockExternalAccountEventPublisher.java` | `AdministrativeActionIntegrationTest.java` | PASS |
| No real external account disablement | `MockExternalAccountEventPublisher.java` | `AdministrativeActionIntegrationTest.java`, source audit | PASS |
| Flyway V7 migration | `V7__advanced_intelligence_hotspots_alerts_sla_trust.sql` | Fresh MySQL Flyway audit | PASS |
| Redis in Docker Compose | `infrastructure/docker-compose.yml` | Redis health check | PASS |
| Hotspot, WebSocket, SLA, Trust Score, privacy and authorization tests | `Phase7IntelligenceIntegrationTest.java`, `AlertWebSocketSessionRegistryTest.java` | `mvn clean verify`, live WebSocket audit | PASS |
| ASP.NET hotspot, alert and escalation dashboards | `official-web-dotnet/Areas/Official/Views/Dashboard/Hotspots.cshtml`, `Alerts.cshtml`, `Escalations.cshtml` | `dotnet build` | PASS |

## Phase 8 - Android, Deployment And Final Delivery

| Requirement | Implementation file | Test file | Result |
| --- | --- | --- | --- |
| Native Android app in `android-app` using Kotlin/Material | `android-app/app/build.gradle.kts`, `MainActivity.kt` | Android build/lint | PASS |
| Citizen registration, OTP verification and login | `MainActivity.kt`, `Phase8ViewModel.kt`, `AaharRakshakApi.kt` | Android unit/build; backend auth tests | PASS |
| Secure JWT storage with Android Keystore/encrypted storage | `android-app/app/src/main/java/com/aaharrakshak/mobile/security/SecureTokenStore.kt` | Android build/lint | PASS |
| Home dashboard | `MainActivity.kt`, `Phase8ViewModel.kt` | Android build/lint | PASS |
| Barcode/QR scan packaged food | `BarcodeScannerAdapter.kt`, `MainActivity.kt` | Android build/lint; backend live barcode scan | PASS |
| CameraX package-image capture | `CameraXPackageCapture.kt`, Android manifest | No emulator/device hardware test | NOT TESTED |
| OCR extraction and correction flow | `OcrAdapter.kt`, `MainActivity.kt`, `Phase8ViewModel.kt` | Android build/lint; backend OCR tests | PASS |
| Prepared-dish and street-food complaint flow | `MainActivity.kt`, `Phase8ViewModel.kt` | Android build/lint; backend live prepared-dish workflow | PASS |
| GPS vendor/location detection with consent | `ConsentLocationProvider.kt`, `MobileFormValidator.kt` | `Phase8ValidationTest.kt`; no device GPS test | PASS |
| Image, video and receipt evidence selection | `MainActivity.kt`, `ApiModels.kt` | Android build/lint | PASS |
| Complaint draft, submission and tracking number | `MobileRepository.kt`, `Phase8ViewModel.kt` | Android build/lint; backend live workflow | PASS |
| Complaint history and status timeline | `AaharRakshakApi.kt`, `MainActivity.kt` | Android build/lint; backend complaint tests | PASS |
| Laboratory-report PDF viewing | `MainActivity.kt`, `AaharRakshakApi.kt` | Android build/lint; no device PDF viewer test | NOT TESTED |
| Public product, company, licence and batch lookup | `AaharRakshakApi.kt`, `MainActivity.kt` | Android build/lint; backend public lookup tests | PASS |
| Recall and regional safety-alert screens | `MainActivity.kt`, `RealtimeAlertClient.kt` | Android build/lint; backend live alert workflow | PASS |
| Hotspot map using OpenStreetMap | `MainActivity.kt` | Android build/lint; no device map rendering test | NOT TESTED |
| Vendor Trust Score and receipt-verified reviews | `AaharRakshakApi.kt`, `MainActivity.kt` | Android build/lint; backend Trust Score tests | PASS |
| WebSocket/push notification support with mock fallback | `RealtimeAlertClient.kt`, `NotificationAdapter.kt` | Android build/lint; backend live WebSocket audit | PASS |
| Room offline complaint drafts | `AaharRakshakDatabase.kt`, `ComplaintDraftDao.kt`, `OfflineComplaintDraftEntity.kt` | Android build/lint | PASS |
| English and Hindi resources | `android-app/app/src/main/res/values/strings.xml`, `values-hi/strings.xml` | Android lint | PASS |
| Accessibility, validation, loading and error states | `MainActivity.kt`, `MobileFormValidator.kt` | Android lint/unit tests | PASS |
| Low-bandwidth handling | `MobileRepository.kt`, `Phase8ViewModel.kt` | Android build/lint; no network throttling test | NOT TESTED |
| Privacy and consent screens | `MainActivity.kt`, `MobileFormValidator.kt` | `Phase8ValidationTest.kt` | PASS |
| Images never confirm chemical adulteration | `MobileFormValidator.kt`, `MainActivity.kt` | `Phase8ValidationTest.kt` | PASS |
| Backend and portal integration with Spring APIs | `AaharRakshakApiClient.cs`, `AaharRakshakApi.kt`, CORS config | `dotnet test`, Android build, backend tests | PASS |
| Production-safe environment config | `backend-spring/src/main/resources/application-prod.yml`, `infrastructure/.env.example` | Source audit | PASS |
| Secrets outside source control | `.gitignore`, `infrastructure/.env.example` | Source scan; `.gitignore` updated | PASS |
| Rate limiting, secure headers and upload protection | `RateLimitingFilter.java`, `SecurityConfig.java`, validators | `RateLimitingFilterTest.java`, validator tests | PASS |
| Health checks, structured logs and error monitoring | `HealthController.java`, `ErrorMonitoringFilter.java`, `logback-spring.xml` | Health tests and live health | PASS |
| Privacy redaction and role authorization | `TransparencyService.java`, `SecurityConfig.java` | Backend integration tests and live role checks | PASS |
| Production Dockerfiles | `backend-spring/Dockerfile`, `official-web-dotnet/Dockerfile` | Docker image build | PASS |
| Compose stack for Spring Boot, MySQL, Redis and MinIO | `infrastructure/docker-compose.yml` | Docker config and service health | PASS |
| Environment-variable templates without real secrets | `infrastructure/.env.example`, `application-prod.yml` | Source audit | PASS |
| Deployment documentation | `docs/deployment.md` | Documentation audit | PASS |
| Free hosting options and limitations | `docs/deployment.md` | Documentation audit | PASS |
| Free provider subdomain configuration notes | `docs/deployment.md` | Documentation audit | PASS |
| Optional custom `aaharrakshak.in` domain instructions | `docs/deployment.md` | Documentation audit | PASS |
| No public deploy, purchase or paid resource | Project boundaries and audit | Source/command audit | PASS |
| Android debug APK generated | `android-app/app/build/outputs/apk/debug/app-debug.apk` | Android build | PASS |
| Complete README and final docs | `README.md`, `docs/*`, final audit docs | Documentation audit | PASS |
| Architecture and block diagrams | `docs/architecture.md` | Documentation audit | PASS |
| ER diagram | `database/schema.md`, `docs/architecture.md` | Documentation audit | PASS |
| API documentation | `docs/api.md`, Swagger/OpenAPI, Postman | `/v3/api-docs`, `jq empty` | PASS |
| Installation/deployment guide | `docs/deployment.md` | Documentation audit | PASS |
| Citizen, company and official manuals | `docs/user-manual-citizen.md`, `docs/user-manual-company.md`, `docs/user-manual-official.md` | Documentation audit | PASS |
| Test report | `docs/test-report.md`, `FINAL_TEST_REPORT.md` | Final audit | PASS |
| Demo credentials and sample data | `DEMO_CREDENTIALS_AND_SAMPLE_DATA.md` | Final audit | PASS |
| Five-minute demonstration script | `FINAL_DEMO_PROCEDURE.md`, `docs/demo-script.md` | Final audit | PASS |
| Interview/viva explanation | `docs/viva-explanation.md` | Documentation audit | PASS |
| Known limitations and future scope | `KNOWN_LIMITATIONS.md`, `docs/known-limitations-future-scope.md` | Final audit | PASS |
| Final project status | `PROJECT_STATUS.md` | Final audit update | PASS |
