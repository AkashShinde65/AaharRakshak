# AaharRakshak Final Test Report

Audit date: 2026-07-23.

Scope: final release audit for Phases 1-8. The audit re-read `AaharRakshak_PROJECT_BRIEF.md`, `AGENTS.md` and `PROJECT_STATUS.md`, preserved existing functionality, fixed one release hygiene issue in `.gitignore`, and did not add new product features.

## Summary

| Area | Result | Evidence |
| --- | --- | --- |
| Spring Boot backend | PASS | `mvn clean verify` passed, 33 tests, jar rebuilt |
| ASP.NET Core MVC portal | PASS | `dotnet build` passed with 0 warnings/errors; `dotnet test` passed, 5 tests |
| Android app | PASS | `gradle test lint assembleDebug` passed with JDK 17 |
| Docker Compose config | PASS | `docker compose -f infrastructure/docker-compose.yml config` passed |
| Docker service health | PASS | MySQL, Redis and MinIO containers healthy |
| Flyway V1-V7 on MySQL | PASS | Fresh schema `aaharrakshak_release_audit` migrated to version 7 |
| Spring health endpoints | PASS | `/api/v1/health` and `/actuator/health` returned `UP` |
| End-to-end complaint workflow | PASS | Live complaint-to-public-report workflow completed |
| WebSocket authentication | PASS | Unauthenticated connection denied; JWT connection opened |
| Swagger/OpenAPI | PASS | `/v3/api-docs` returned HTTP 200 |
| Postman examples | PASS | Phase 4-8 Postman JSON collections parsed with `jq` |
| Android hardware/manual UI walkthrough | NOT TESTED | Build, unit tests and lint passed; no emulator/device session was run |
| Browser responsive visual QA | NOT TESTED | Portal build/tests passed; no Playwright/browser visual regression suite exists |

## Commands Run

| Command | Working directory | Result |
| --- | --- | --- |
| `mvn clean verify` | `backend-spring/` | PASS, Tests run: 33, Failures: 0, Errors: 0, Skipped: 0 |
| `dotnet build official-web-dotnet/official-web-dotnet.csproj` | repo root | PASS, 0 warnings, 0 errors |
| `dotnet test official-web-dotnet.Tests/official-web-dotnet.Tests.csproj` | repo root | PASS, 5 tests |
| `JAVA_HOME=/usr/local/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home gradle test lint assembleDebug` | `android-app/` | PASS |
| `docker compose -f infrastructure/docker-compose.yml config` | repo root | PASS |
| `docker compose -f infrastructure/docker-compose.yml up -d mysql redis minio` | repo root | PASS |
| `docker compose -f infrastructure/docker-compose.yml ps` | repo root | PASS, MySQL/Redis/MinIO healthy |
| `docker compose -f infrastructure/docker-compose.yml exec -T redis redis-cli ping` | repo root | PASS, `PONG` |
| `curl -fsS http://localhost:9000/minio/health/live` | repo root | PASS |
| `java -jar target/backend-spring-0.0.1-SNAPSHOT.jar` with Docker MySQL/Redis env | `backend-spring/` | PASS, API started |
| `curl -fsS http://localhost:8080/api/v1/health` | repo root | PASS, service `UP` |
| `curl -fsS http://localhost:8080/actuator/health` | repo root | PASS, actuator `UP` |
| `curl -fsS http://localhost:8080/v3/api-docs` | repo root | PASS, HTTP 200 |
| `jq empty postman/AaharRakshak_Phase*.postman_collection.json` | repo root | PASS |
| `docker compose -f infrastructure/docker-compose.yml build backend-api official-web` | repo root | PASS |

## Live MySQL And Flyway Verification

Fresh database: `aaharrakshak_release_audit`.

| Version | Description | Result |
| --- | --- | --- |
| 1 | initial schema | PASS |
| 2 | authentication and security | PASS |
| 3 | company licence product batch management | PASS |
| 4 | citizen complaint scanning system | PASS |
| 5 | official investigation sample lab reporting | PASS |
| 6 | transparency administrative action web portals | PASS |
| 7 | advanced intelligence hotspots alerts sla trust | PASS |

The live audit database contained 43 audit-log records after workflow execution, including registration/login-related actions from the seeded users and workflow records for complaint submission, inspector assignment, inspection, sample custody, lab report publication, show-cause notice, company response, administrative action, hotspot detection and SLA escalation.

## End-To-End Workflow

The live workflow was executed against Spring Boot on `localhost:8080`, Docker MySQL, Docker Redis and Docker MinIO.

| Step | Result | Evidence |
| --- | --- | --- |
| Citizen login | PASS | JWT issued for `citizen@aaharrakshak.dev` |
| Packaged-food barcode scan | PASS | `barcodeMatched=true` for `8901234567890` |
| Draft and submit packaged-food complaint | PASS | Tracking number `ARK-20260723-934679` |
| Draft and submit prepared-dish complaint | PASS | Tracking number `ARK-20260723-299931`; vendor and dish image metadata supplied |
| Citizen privacy role check | PASS | Citizen denied official endpoint with HTTP 403 |
| District assignment | PASS | Complaint moved to `ASSIGNED` |
| Inspector scoped access | PASS | Inspector viewed assigned case; lab user denied assigned complaint access |
| Inspection schedule, check-in and visit record | PASS | Visit completed with geotag and evidence metadata |
| Sample collection and custody | PASS | Sample `SMP-20260723-210511` created with custody history |
| Lab assignment and receipt | PASS | Assigned lab officer confirmed `RECEIVED` |
| Lab report draft, submit, review and publish | PASS | Report `LAB-AUDIT-1784810048` reached `PUBLISHED` |
| Show-cause notice | PASS | Notice `SCN-20260723-955877` issued |
| Company response | PASS | Document metadata and checksum accepted |
| Senior-official decision | PASS | Simulated `BATCH_RECALL`, action `ADM-20260723-137105` |
| Public complaint tracking | PASS | Public status reached `REPORT_PUBLISHED` for the live complaint |
| Public report redaction | PASS | No citizen name, email or mobile exposed |
| Public recall listing | PASS | Recall list contained the simulated batch recall action |
| Hotspot detection | PASS | Critical hotspot detected for seeded Pune cluster |
| SLA escalation | PASS | Overdue high-risk seed complaint escalated |
| Trust Score review | PASS | Receipt-backed review submitted; public Trust Score showed `reviewCount=1` and fairness note |
| Mock external event | PASS | Mock batch recall event recorded; no real external action performed |

## Security And Privacy Audit

| Check | Result | Evidence |
| --- | --- | --- |
| Authentication and role permissions | PASS | Backend integration tests plus live 403/200 role checks |
| JWT-protected Swagger/OpenAPI use | PASS | OpenAPI available; security configured in `OpenApiConfig` |
| BCrypt and refresh-token logic | PASS | Covered by `AuthSecurityIntegrationTest` |
| Mock OTP and mock Aadhaar boundary | PASS | API stores status/token only; no full Aadhaar input DTO exists |
| Citizen privacy and anonymized reports | PASS | `AdministrativeActionIntegrationTest`, Phase 7 privacy tests and live public responses |
| Evidence and report checksum validation | PASS | Backend validator tests and live workflow metadata |
| File upload protection | PASS | Content type, size, checksum and path metadata validators tested |
| Secrets in source | PASS | No real `.env`, keystore, private key or production secret found; demo placeholders documented |
| Local build artifacts in release | PASS | `.gitignore` expanded and source zip excludes build/secrets paths |

## Generated Artifacts

| Artifact | Location | Result |
| --- | --- | --- |
| Spring jar | `backend-spring/target/backend-spring-0.0.1-SNAPSHOT.jar` | PASS |
| Android debug APK | `android-app/app/build/outputs/apk/debug/app-debug.apk` | PASS |
| Android APK SHA-256 | `d06d5e581e6720f95d428a86e97b20ae0c70390937a84e30a8683356f013f12e` | PASS |
| Clean source archive | `AaharRakshak-source.zip` | Created after final docs; hash reported in final response |

## Untested Scope

- No real Android device/emulator session was run, so camera permissions, GPS provider behavior, native barcode scanning and PDF viewing are build/lint verified but not hardware verified.
- No browser visual regression test was run for portal responsive behavior.
- No real Aadhaar, government registry, SMS, push-notification vendor, payment, delivery or storefront account was integrated or called.
- No public deployment or provider subdomain creation was performed.
