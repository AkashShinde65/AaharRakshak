# Phase 2 Authentication And Security

Phase 2 adds Spring Security, BCrypt passwords, signed JWT access tokens, hashed refresh tokens, role authorization and mock verification flows.

Swagger/OpenAPI defines a `bearerAuth` JWT scheme. Use the **Authorize** button in Swagger UI with a login access token.

## Demo Credentials

All development seed users use password `password`.

| Role | Email |
| --- | --- |
| Citizen | `citizen@aaharrakshak.dev` |
| Company | `company@aaharrakshak.dev` |
| Food Inspector | `inspector@aaharrakshak.dev` |
| Laboratory Officer | `lab@aaharrakshak.dev` |
| District Officer | `district@aaharrakshak.dev` |
| Central Administrator | `admin@aaharrakshak.dev` |

## Authentication APIs

- `POST /api/v1/auth/register/citizen`
- `POST /api/v1/auth/register/company`
- `POST /api/v1/auth/login`
- `POST /api/v1/auth/refresh`
- `POST /api/v1/auth/otp/request`
- `POST /api/v1/auth/otp/verify`
- `POST /api/v1/auth/mock-aadhaar/verify`
- `GET /api/v1/auth/me`

Mock OTP code is always `123456`.

Mock Aadhaar verification accepts consent only and stores a verification status/token. The API must never accept or persist a full Aadhaar number, Aadhaar document or biometric data.

Login attempts are counted for known accounts. After the configured maximum, the account is temporarily locked and login failures are audited.

## Role-Protected Demo APIs

- `GET /api/v1/citizen/profile` requires `CITIZEN`
- `GET /api/v1/company/account` requires `COMPANY`
- `GET /api/v1/official/inspectors/dashboard` requires `FOOD_INSPECTOR`
- `GET /api/v1/official/lab/dashboard` requires `LABORATORY_OFFICER`
- `GET /api/v1/official/district/dashboard` requires `DISTRICT_ESCALATION_OFFICER`
- `GET /api/v1/admin/dashboard` requires `CENTRAL_ADMINISTRATOR`
- `POST /api/v1/admin/companies/{companyId}/verify` requires `CENTRAL_ADMINISTRATOR`

## Phase 3 Company And Product APIs

- `GET /api/v1/company/profile` requires `COMPANY`
- `PUT /api/v1/company/profile` requires `COMPANY`
- `POST /api/v1/company/licences` requires `COMPANY`
- `GET /api/v1/company/licences` requires `COMPANY`
- `POST /api/v1/company/products` requires `COMPANY`
- `GET /api/v1/company/products` requires `COMPANY`
- `GET /api/v1/company/products/{productId}` requires owning `COMPANY`
- `PUT /api/v1/company/products/{productId}` requires owning `COMPANY`
- `POST /api/v1/company/products/{productId}/batches` requires owning `COMPANY`
- `GET /api/v1/company/products/{productId}/batches` requires owning `COMPANY`
- `PUT /api/v1/company/batches/{batchId}` requires owning `COMPANY`
- `POST /api/v1/official/licences/{licenceId}/verify` requires `FOOD_INSPECTOR`, `DISTRICT_ESCALATION_OFFICER` or `CENTRAL_ADMINISTRATOR`
- `POST /api/v1/official/licences/{licenceId}/reject` requires `FOOD_INSPECTOR`, `DISTRICT_ESCALATION_OFFICER` or `CENTRAL_ADMINISTRATOR`
- `POST /api/v1/official/licences/{licenceId}/expire` requires `FOOD_INSPECTOR`, `DISTRICT_ESCALATION_OFFICER` or `CENTRAL_ADMINISTRATOR`
- `GET /api/v1/public/products/barcodes/{barcode}` is public
- `GET /api/v1/public/products/search?query={name}` is public

## Phase 4 Citizen Complaint APIs

- `POST /api/v1/citizen/scans/packaged-food` requires `CITIZEN`
- `POST /api/v1/citizen/complaints/drafts` requires `CITIZEN`
- `PUT /api/v1/citizen/complaints/{complaintId}/draft` requires owning `CITIZEN`
- `POST /api/v1/citizen/complaints/{complaintId}/evidence` requires owning `CITIZEN`
- `POST /api/v1/citizen/complaints/{complaintId}/submit` requires owning `CITIZEN`
- `GET /api/v1/citizen/complaints` returns only the authenticated citizen's complaints
- `GET /api/v1/citizen/complaints/{ticketNumber}` requires owning `CITIZEN`
- `GET /api/v1/official/complaints/assigned` requires an official role and returns only assigned complaints
- `GET /api/v1/official/complaints/{ticketNumber}` requires assignment for inspectors/lab officers; district officers and central administrators have oversight access

Phase 4 complaint responses do not expose citizen email, mobile number or private identity fields. Companies and public users do not receive citizen complaint details. OCR and image metadata are treated only as triage aids; the API message explicitly avoids claiming that an image proves adulteration.

## Phase 5 Investigation APIs

- `GET /api/v1/official/investigations/dashboard` requires `FOOD_INSPECTOR`, `DISTRICT_ESCALATION_OFFICER` or `CENTRAL_ADMINISTRATOR`; inspectors receive only assigned complaints.
- `GET /api/v1/official/investigations/assigned` requires `FOOD_INSPECTOR`.
- `GET /api/v1/official/investigations/complaints/{ticketNumber}` requires assignment for inspectors; district officers and central administrators have oversight access.
- `POST /api/v1/official/investigations/complaints/{ticketNumber}/verify` requires `DISTRICT_ESCALATION_OFFICER` or `CENTRAL_ADMINISTRATOR`.
- `POST /api/v1/official/investigations/complaints/{ticketNumber}/assign-inspector` requires `DISTRICT_ESCALATION_OFFICER` or `CENTRAL_ADMINISTRATOR`.
- `POST /api/v1/official/investigations/complaints/{ticketNumber}/inspections/schedule` requires assigned `FOOD_INSPECTOR` or senior official oversight.
- `POST /api/v1/official/investigations/inspections/{inspectionId}/check-in` requires the assigned inspector or senior official oversight.
- `POST /api/v1/official/investigations/inspections/{inspectionId}/visit-record` requires the assigned inspector or senior official oversight.
- `POST /api/v1/official/investigations/inspections/{inspectionId}/samples` requires the assigned inspector or senior official oversight.
- `POST /api/v1/official/investigations/samples/{sampleId}/assign-lab` requires `DISTRICT_ESCALATION_OFFICER` or `CENTRAL_ADMINISTRATOR`.
- `GET /api/v1/lab/investigations/samples/assigned` requires `LABORATORY_OFFICER`.
- `POST /api/v1/lab/investigations/samples/{sampleId}/received` requires the assigned `LABORATORY_OFFICER`.
- `POST /api/v1/lab/investigations/samples/{sampleId}/reports/drafts` requires the assigned `LABORATORY_OFFICER`.
- `POST /api/v1/lab/investigations/reports/{reportId}/submit` requires the assigned `LABORATORY_OFFICER`.
- `POST /api/v1/official/investigations/lab-reports/{reportId}/review` requires `DISTRICT_ESCALATION_OFFICER` or `CENTRAL_ADMINISTRATOR`.
- `POST /api/v1/official/investigations/lab-reports/{reportId}/publish` requires `DISTRICT_ESCALATION_OFFICER` or `CENTRAL_ADMINISTRATOR`.

Investigation notifications to citizens are status-only and do not expose inspector names, lab officer names, internal notes or enforcement strategy. Report publication records an audit entry and explicitly does not impose an automatic real licence ban.

## Phase 6 Administrative Action And Transparency APIs

- `GET /api/v1/official/admin-actions/dashboard` requires `DISTRICT_ESCALATION_OFFICER` or `CENTRAL_ADMINISTRATOR`.
- `GET /api/v1/official/admin-actions/notices` requires `DISTRICT_ESCALATION_OFFICER` or `CENTRAL_ADMINISTRATOR`.
- `GET /api/v1/official/admin-actions/notices/{noticeNumber}` requires `DISTRICT_ESCALATION_OFFICER` or `CENTRAL_ADMINISTRATOR`.
- `POST /api/v1/official/admin-actions/reports/{reportId}/show-cause-notices` requires `DISTRICT_ESCALATION_OFFICER` or `CENTRAL_ADMINISTRATOR`.
- `POST /api/v1/official/admin-actions/notices/{noticeNumber}/review` requires `DISTRICT_ESCALATION_OFFICER` or `CENTRAL_ADMINISTRATOR`.
- `POST /api/v1/official/admin-actions/notices/{noticeNumber}/decision` requires `DISTRICT_ESCALATION_OFFICER` or `CENTRAL_ADMINISTRATOR`.
- `GET /api/v1/company/admin-actions/notices` requires `COMPANY` and returns only notices for the authenticated user's company.
- `GET /api/v1/company/admin-actions/notices/{noticeNumber}` requires owning `COMPANY`.
- `POST /api/v1/company/admin-actions/notices/{noticeNumber}/responses` requires owning `COMPANY`.
- `GET /api/v1/public/transparency/complaints/{ticketNumber}/status` is public but returns only public-safe status/report fields.
- `GET /api/v1/public/transparency/reports/{reportNumber}` is public and anonymized.
- `GET /api/v1/public/transparency/search` is public.
- `GET /api/v1/public/transparency/licences/{licenceNumber}/status` is public.
- `GET /api/v1/public/transparency/batches/{batchNumber}/status` is public.
- `GET /api/v1/public/transparency/recalls` is public.
- `GET /api/v1/public/transparency/alerts` is public.

Lab officers and inspectors cannot approve administrative actions. The service also checks the report submitter and assigned inspector so a user who participated in a case cannot approve that same case if roles are ever combined in development data.

Public report and search responses exclude citizen name, email, mobile number, identity verification fields, full complaint evidence internals, exact sensitive official notes and chain-of-custody internals. Company responses and public reports retain document/report checksums for tamper detection. Suspension and cancellation are simulated platform records only and never perform a real government registry action.

## Phase 7 Intelligence, Alerts And SLA APIs

- `GET /api/v1/official/intelligence/hotspots/district?district={district}` requires an official role and returns aggregate district-level hotspots.
- `POST /api/v1/official/intelligence/hotspots/detect?district={district}` requires an official role and recalculates aggregate hotspots.
- `GET /api/v1/official/intelligence/sla/escalations` requires an official role.
- `POST /api/v1/official/intelligence/sla/check-overdue` requires `DISTRICT_ESCALATION_OFFICER` or `CENTRAL_ADMINISTRATOR`.
- `POST /api/v1/official/intelligence/complaints/{ticketNumber}/close` blocks assigned inspectors from silently closing verified high-risk cases.
- `POST /api/v1/official/intelligence/risk/complaints/{ticketNumber}` requires an official role and creates an explainable mock risk analysis.
- `GET /api/v1/official/intelligence/alerts/outbox` requires `DISTRICT_ESCALATION_OFFICER` or `CENTRAL_ADMINISTRATOR`.
- `POST /api/v1/official/intelligence/alerts/retry` requires `DISTRICT_ESCALATION_OFFICER` or `CENTRAL_ADMINISTRATOR`.
- `GET /api/v1/official/intelligence/mock-external-events` requires `DISTRICT_ESCALATION_OFFICER` or `CENTRAL_ADMINISTRATOR`.
- `POST /api/v1/citizen/trust/reviews` requires `CITIZEN` and requires receipt metadata.
- `GET /api/v1/citizen/alerts` requires `CITIZEN` and returns only the authenticated citizen's alerts.
- `GET /api/v1/public/trust/companies/{companyId}` is public and includes the raw-complaint fairness note.
- `GET /ws/alerts?access_token={jwt}` is an authenticated WebSocket endpoint for real-time alert delivery.

Hotspot and public Trust Score responses are privacy-preserving. Hotspots expose only aggregate centers and counts, not citizen GPS points. Trust Score uses verified inspections, laboratory outcomes, recalls and receipt-backed reviews; raw complaints alone do not prove guilt. Camera/OCR and mock AI outputs must never claim chemical adulteration without laboratory confirmation.

Redis Pub/Sub is used for real-time demo fan-out, while `alert_outbox` remains the durable source for retry. Email, push and SMS are mock adapter interfaces in this phase. Mock external storefront, delivery and payment events are stored as simulated events only and never disable a real external account or service.

## ASP.NET Core MVC Portal Security

The `official-web-dotnet/` portal uses cookie authentication for the browser session and stores the Spring JWT access token as a server-side auth claim in an HTTP-only cookie. MVC forms use anti-forgery tokens. The portal consumes Spring Boot REST APIs through `HttpClient` and relies on the backend for workflow authorization and validation. Role-based navigation separates Official, Company and Public Areas.

## Phase 8 Mobile, Deployment And Runtime Security

- Android stores JWT access and refresh tokens through `SecureTokenStore`, which encrypts token values with an Android Keystore AES-GCM key before writing to app-private shared preferences.
- Android offline complaint drafts are stored in Room and are excluded from cloud backup/data extraction rules.
- Android GPS data is captured only after explicit consent and is used for routing/regional alerts; public hotspot views use aggregate centers only.
- Android camera, OCR and barcode outputs are client-side aids. Users must correct or confirm details, and images never confirm chemical adulteration without lab testing.
- Spring CORS is configured by `CORS_ALLOWED_ORIGINS`, covering local portal, Android emulator and future hosted subdomains.
- Spring adds secure response headers: content security policy, no-referrer, restrictive permissions policy and default Spring Security hardening.
- Spring rate limiting is controlled by `RATE_LIMIT_ENABLED`, `RATE_LIMIT_MAX_REQUESTS` and `RATE_LIMIT_WINDOW_SECONDS`.
- Spring upload protection uses environment-configured request limits plus existing validators for content type, file size, object keys, checksums and PDF/image/video safety.
- ASP.NET Core adds global anti-forgery validation for unsafe methods, HTTP-only strict cookies, secure cookie policy outside development and browser security headers.
- Docker/production configs use environment variables only. `.env.example` contains placeholders and must not be filled with real secrets in version control.

## Production Checklist

- Replace `JWT_SECRET`, database passwords and MinIO credentials with generated secrets.
- Serve API and portal over HTTPS only.
- Set `CORS_ALLOWED_ORIGINS` to exact portal/mobile deep-link origins, not wildcards.
- Use real object storage with private buckets and short-lived upload/download URLs before production evidence uploads.
- Connect monitored email/SMS/push providers only after legal and consent review.
- Keep real Aadhaar, unauthorized government data and real licence enforcement outside this academic demo.
