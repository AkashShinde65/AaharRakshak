# AaharRakshak Project Brief

## 1. Project identity

- **Name:** AaharRakshak
- **Full title:** AaharRakshak – National Food Safety and Adulteration Reporting Platform
- **Tagline:** Scan Karo. Report Karo. Surakshit Raho.
- **Purpose:** Make food-safety complaints simple, evidence-based, location-aware and transparent from citizen submission through official investigation and publication of the final report.

## 2. Academic and technical requirements

This is a complex C-DAC final project intended to demonstrate Advanced Java, .NET, databases, REST APIs, deployment and interview-ready architecture.

- Follow SOLID principles, layered architecture, DTOs, validation, exception handling and clean code.
- Build in small phases; keep every completed phase runnable and tested.
- Use Git only if available. GitHub Student is not required.
- Prefer free development and hosting services for the demonstration.

## 3. User applications

1. **Citizen Android app** for scanning food, submitting complaints, tracking status and viewing public reports.
2. **Official web portal** with separate secure login and full role-based access for inspectors, lab officers, district officers and central administrators.
3. **Company/FBO web portal** for registration, licence details, products, batches, notices and responses.
4. **Public transparency portal** for safe public information, verified reports, recalls, licence status and anonymized statistics.

## 4. Core roles

- Citizen
- Food Business Operator / Company
- Food Inspector
- Laboratory Officer
- District Escalation Officer
- Central Administrator

Use role-based access control. Officials receive only the access needed for their role. Record important official actions in an immutable-style audit log.

## 5. Easy citizen complaint flow

The citizen should not have to manually search for a company or type every product detail.

### Packaged food check

1. User scans barcode/QR code and photographs the front label, FSSAI licence area, batch/lot number, manufacturer, expiry date and purchase receipt.
2. Barcode lookup and OCR extract product, company, licence, batch and date information.
3. User reviews and corrects the detected details instead of entering everything manually.
4. App captures GPS location with user consent.
5. User selects a short complaint category, adds optional text, image or video and submits.

### Prepared dish / loose food check

Restaurants, street food and loose dishes may not have a package or batch number.

1. User photographs or records the dish, shop/signboard and optional receipt.
2. GPS identifies the place; map search suggests the vendor.
3. User selects symptoms or suspicion and submits minimal details.
4. Camera-based AI may classify visible food/type or quality warning signs, but it must never claim that an image alone proves chemical adulteration. Confirmation requires inspection and laboratory testing.

## 6. Identity and privacy

- Verify a citizen using mobile/email OTP. For the academic demo, use a mock identity-verification service.
- If Aadhaar is demonstrated, use consent-based, masked/mock verification and store only a verification token/status. Do not store the full Aadhaar number, Aadhaar image or biometric data.
- A real production Aadhaar integration and real government registry integration require authorization and applicable legal/privacy compliance.
- Citizen identity and contact details are encrypted/restricted and visible only to specifically authorized officials for legitimate case handling.
- Companies and the public must never see the complainant’s private details.
- Public reports must be anonymized.
- Add consent, privacy notice, retention rules, access logs and account/data-deletion handling.

## 7. Complaint and investigation workflow

Use these main states:

`DRAFT -> SUBMITTED -> VERIFIED -> ASSIGNED -> INSPECTION_SCHEDULED -> SAMPLE_COLLECTED -> LAB_TESTING -> REPORT_PUBLISHED -> ACTION_TAKEN -> CLOSED`

Alternative outcomes include `REJECTED_DUPLICATE`, `INSUFFICIENT_EVIDENCE`, `NO_VIOLATION_FOUND` and `ESCALATED`.

Workflow:

1. Citizen submits evidence and receives a complaint/ticket number.
2. System detects possible duplicate complaints and groups related cases without deleting individual evidence.
3. Risk engine prioritizes cases using complaint count, product category, symptoms, affected region and time window.
4. Map routes the assigned official to the complaint/vendor location.
5. Inspector records visit, geotag, notes, evidence and sample seal/chain-of-custody details.
6. Laboratory officer uploads a digitally traceable food-testing report.
7. Authorized official reviews the result and records the legal/administrative decision.
8. A verified public-safe version of the report and action becomes visible in the app.
9. Citizen receives status updates by in-app notification, email or mobile number.

The software may simulate licence suspension for the academic project. In a real system, only the legally authorized government authority can suspend or cancel a licence; the platform must not automatically impose a real legal penalty without due process.

## 8. Advanced modules

- Complaint hotspot cluster map using OpenStreetMap/Leaflet first; Google Maps can be optional.
- Configurable outbreak rule, for example multiple related complaints in the same area and time window.
- Recall and location-based alert system using Redis Pub/Sub and WebSockets; SMS can use a mock provider during the demo.
- Public Trust Score for vendors based on verified inspections, test outcomes and receipt-verified ratings—not raw allegations alone.
- Receipt OCR to unlock verified-purchase reviews.
- SLA timer: overdue high-risk cases escalate to a district dashboard and create an audit entry.
- Duplicate/fraud detection, rate limiting and moderation.
- Multilingual-ready interface for major Indian languages.
- Accessibility, low-bandwidth mode and offline complaint draft.

## 9. Data strategy

- Do not depend on unauthorized access to government databases.
- For the academic demo, use seeded/mock companies, products, licences, batches and laboratory reports.
- Import only clearly public/open datasets under their permitted terms and record the source/licence.
- Allow companies to register and submit data, but keep their status `PENDING_VERIFICATION` until verified.
- Barcode/OCR can extract package details, but the user must confirm uncertain fields.
- The system cannot recognize every product in India from an image unless a reliable catalogue/integration exists; design graceful manual correction and unknown-product reporting.

## 10. Recommended implementation stack

### Backend

- Java 21
- Spring Boot 3
- Maven
- Spring Web REST APIs
- Spring Security + JWT/refresh tokens
- Spring Data JPA / Hibernate
- Bean Validation
- OpenAPI/Swagger
- MySQL 8
- Flyway database migrations
- Redis for caching, queues/pub-sub and SLA support
- WebSocket notifications
- MinIO locally for image/video/report storage; an S3-compatible service can be used in deployment

Start as a modular monolith with clear modules. Extract microservices only after core workflows are stable and when there is a defensible scaling reason.

### Web

- Official/company/public portal may use ASP.NET Core MVC to demonstrate the .NET syllabus while consuming the Spring Boot REST API.
- Use Bootstrap for a responsive interface.
- Never duplicate business rules in the web portal; enforce them in the API.

### Android

- Native Android using Kotlin (Java is acceptable if required by the course)
- Retrofit/OkHttp for REST
- CameraX
- ML Kit/Tesseract OCR
- ZXing or ML Kit barcode scanner
- Fused Location Provider
- Room for offline drafts
- Firebase Cloud Messaging where available; provide a mock/local notification path for the demo

### DevOps

- Docker and Docker Compose
- Spring Boot tests, JUnit, Mockito and integration tests
- Postman/Bruno API collection
- CI can use ordinary free GitHub Actions if a GitHub account is later connected
- Free service subdomains may be used initially; custom domains are usually paid

## 11. Suggested repository layout

```text
AaharRakshak/
  docs/
  backend-spring/
  official-web-dotnet/
  android-app/
  database/
  infrastructure/
  postman/
  README.md
```

## 12. First Codex task

Read this entire brief before editing. Then complete **Phase 1 only**:

1. Create `README.md` and `docs/architecture.md`.
2. Create a simple block/flow diagram using Mermaid.
3. Design the normalized MySQL schema and ER diagram.
4. Scaffold a runnable Spring Boot backend with health endpoint, environment-based configuration and OpenAPI.
5. Create initial entities/modules for users, roles, companies, licences, products, batches, complaints, evidence, assignments, samples, lab reports, actions, notifications and audit logs.
6. Add Flyway migrations and Docker Compose for MySQL.
7. Add basic tests and run the Maven build.
8. Report created files, commands run, test results, assumptions and the next phase.

Do not implement real Aadhaar, real licence suspension, paid SMS, proprietary government APIs or unverified AI claims in Phase 1. Use interfaces and mock adapters so authorized integrations can be added later.

