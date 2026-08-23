# AaharRakshak Project Status

Current status: Phase 8 - Android Application, Deployment and Final Delivery is complete, and the Final Release Audit is complete.

## Completed

- Phase 1 foundation: docs, Spring Boot backend, health endpoint, OpenAPI, Flyway V1 and Docker MySQL setup.
- Phase 2 authentication/security: JWT, refresh tokens, BCrypt, RBAC, OTP, mock Aadhaar status/token, audit logs and seed users.
- Phase 3 company/licence/catalogue: company profile, FSSAI format validation, mock licence registry, products, barcodes and batches.
- Phase 4 citizen complaint/scanning: barcode/OCR-assisted drafts, evidence metadata validation, GPS consent, status history and citizen/official complaint access.
- Phase 5 investigation/lab: priority dashboard, assignment, inspection visits, geotagging, samples, custody, lab assignment, PDF report metadata and report publication.
- Phase 6 transparency/action/portal: lab outcomes, show-cause notices, company responses, senior-official decisions, simulated actions, anonymized public transparency APIs, recall/safety alerts and ASP.NET Core MVC portal.
- Phase 7 intelligence/alerts/SLA/trust: hotspot detection, Leaflet/OpenStreetMap portal map, Redis Pub/Sub, authenticated WebSocket alerts, durable alert outbox/retry, SLA escalation, Trust Score, verified receipt-backed reviews, mock AI risk analysis and mock external lockdown events.
- Phase 8 Android/deployment/final delivery: native Kotlin/Material Android app, secure JWT storage, scan/OCR/correction flows, prepared-dish complaints, GPS consent, evidence selection, offline Room drafts, complaint tracking, public lookup, lab PDF viewing, recall/safety alerts, OpenStreetMap hotspot screen, Trust Score/reviews, mock WebSocket/push fallback, English/Hindi resources, backend CORS/rate-limit/secure-header hardening, structured logs, health probes, local Micrometer error metrics, production Dockerfiles, complete Compose stack with MySQL/Redis/MinIO, environment templates, deployment documentation and final user/demo/viva/test documentation.
- Final Release Audit: requirement traceability, release checklist, final test report, limitations, demo credentials/sample data, five-minute demo procedure, source packaging hygiene and clean source archive preparation.

## Final Release Audit Verification Status

Last verified on 2026-07-23.

- Backend: `mvn clean verify` passed with 33 tests and rebuilt `backend-spring-0.0.1-SNAPSHOT.jar`.
- Docker config: `docker compose -f infrastructure/docker-compose.yml config` passed for Spring Boot, MySQL, Redis, MinIO and ASP.NET portal services.
- Docker images: `docker compose -f infrastructure/docker-compose.yml build backend-api official-web` passed.
- Docker services: MySQL, Redis and MinIO were healthy; Redis returned `PONG`; MinIO `/minio/health/live` returned success.
- Flyway/MySQL: fresh Docker schema `aaharrakshak_release_audit` validated V1 through V7 successfully and remained at version 7.
- API: rebuilt Spring Boot jar ran against Docker MySQL/Redis and `GET /api/v1/health` plus `GET /actuator/health` returned HTTP 200 with `UP`.
- Portal: `dotnet build official-web-dotnet/official-web-dotnet.csproj` passed with 0 warnings and 0 errors.
- Portal tests: `dotnet test official-web-dotnet.Tests/official-web-dotnet.Tests.csproj` passed with 5 tests.
- Android: `gradle test lint assembleDebug` passed with JDK 17; debug APK generated at `android-app/app/build/outputs/apk/debug/app-debug.apk` with SHA-256 `d06d5e581e6720f95d428a86e97b20ae0c70390937a84e30a8683356f013f12e`.
- Live final workflow: packaged-food complaint `ARK-20260723-934679` and prepared-dish complaint `ARK-20260723-299931` were submitted. The packaged complaint was assigned, inspected, sampled as `SMP-20260723-210511`, received by the lab, published as report `LAB-AUDIT-1784810048`, issued show-cause notice `SCN-20260723-955877`, received a company response, and recorded simulated administrative action `ADM-20260723-137105`.
- Live privacy/security checks: citizen was denied official APIs with HTTP 403, lab officer was denied inspector-only assigned complaint access, public report/search responses were redacted, unauthenticated WebSocket connection failed, JWT WebSocket connection opened, and protected actuator metrics required authentication.
- Live intelligence checks: critical hotspot detection, SLA escalation, receipt-backed Trust Score review and mock external batch-recall event all completed.
- Audit logs: fresh live audit database contained 43 audit-log records covering the final workflow and administrative actions.
- Swagger/Postman: `/v3/api-docs` returned HTTP 200 and Phase 4-8 Postman collections parsed successfully.
- Documentation: README, API guide, deployment guide, architecture notes, manuals, test report, demo script, viva notes, known-limitations/future-scope and final audit documents are present.

## Phase 6 Seed Data

- `ARK-SEED-0006`: public-safe complaint with published report.
- `LAB-SEED-0006`: published mock PDF report metadata with `ADULTERATED` outcome.
- Company, product, batch and licence seed data are inherited from Phase 3.

## Phase 7 Seed Data

- `ARK-HOT-0001` through `ARK-HOT-0010`: related Pune demo complaints for the default critical hotspot rule.
- `ARK-SLA-0007`: overdue high-risk assigned complaint for SLA escalation testing.
- Phase 7 recalculates Trust Score for company `1` from verified inspections, lab outcomes, recalls and receipt-backed reviews.

## Phase 8 Delivery Artifacts

- Android APK: `android-app/app/build/outputs/apk/debug/app-debug.apk`.
- Production Dockerfiles: `backend-spring/Dockerfile` and `official-web-dotnet/Dockerfile`.
- Complete local stack: `infrastructure/docker-compose.yml`.
- Environment template: `infrastructure/.env.example`.
- Final documentation: `README.md`, `docs/deployment.md`, `docs/api.md`, `docs/architecture.md`, `docs/user-manual-citizen.md`, `docs/user-manual-company.md`, `docs/user-manual-official.md`, `docs/test-report.md`, `docs/demo-script.md`, `docs/viva-explanation.md`, `docs/known-limitations-future-scope.md`.

## Final Release Audit Artifacts

- `FINAL_TEST_REPORT.md`
- `RELEASE_CHECKLIST.md`
- `REQUIREMENT_TRACEABILITY.md`
- `KNOWN_LIMITATIONS.md`
- `DEMO_CREDENTIALS_AND_SAMPLE_DATA.md`
- `FINAL_DEMO_PROCEDURE.md`
- Clean source archive: `AaharRakshak-source.zip` generated from the audited source tree, excluding secrets, `.env`, keystores, `target`, `bin`, `obj`, `.gradle` and temporary build files.

## Boundaries

- Phase 8 did not add a new database migration because final delivery work did not require schema changes; Flyway V1 through V7 remain authoritative.
- Final Release Audit did not add product features. It added release audit documentation and tightened ignore rules for local/build/secret artifacts.
- Licence suspension/cancellation remains simulated platform state only.
- Public reports remain anonymized and do not expose citizen private details.
- Hotspots expose aggregate district-level centers only, not citizen GPS locations.
- Mock external events never disable real storefront, delivery or payment accounts.
- Android camera/OCR screens may suggest visible text/category only; images never confirm chemical adulteration without laboratory testing.
- No real Aadhaar, unauthorized government data, real SMS, paid service or public deployment was integrated.
- Android physical device/emulator walkthrough, CameraX hardware behavior, device GPS provider behavior, portal browser visual-regression checks and public deployment remain NOT TESTED in this audit.
- Do not start Phase 9 unless the user explicitly asks for it.
