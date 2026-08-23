# Test Report

Last updated: 2026-07-23.

## Automated Tests

| Area | Command | Current Result |
| --- | --- | --- |
| Spring backend | `mvn clean verify` from `backend-spring/` | PASS, 33 tests, Boot jar rebuilt with local error-monitoring filter |
| ASP.NET build | `dotnet build official-web-dotnet/official-web-dotnet.csproj` | PASS, 0 warnings, 0 errors |
| ASP.NET tests | `dotnet test official-web-dotnet.Tests/official-web-dotnet.Tests.csproj` | PASS, 5 tests |
| Android unit tests | `gradle test` from `android-app/` with JDK 17 | PASS |
| Android lint | `gradle lint` from `android-app/` with JDK 17 | PASS, report at `android-app/app/build/reports/lint-results-debug.html` |
| Android debug APK | `gradle assembleDebug` from `android-app/` with JDK 17 | PASS, APK generated at `android-app/app/build/outputs/apk/debug/app-debug.apk` |
| Docker config | `docker compose -f infrastructure/docker-compose.yml config` | PASS |
| Docker image build | `docker compose -f infrastructure/docker-compose.yml build backend-api official-web` | PASS, backend and portal production images built |
| Docker service health | `docker compose -f infrastructure/docker-compose.yml ps` | PASS, MySQL, Redis and MinIO healthy |
| MySQL Flyway V1-V7 | Docker MySQL + Spring Boot/Flyway | PASS, schema `aaharrakshak_phase8_verify` at version 7 |
| Spring health endpoint | `GET /api/v1/health` and `GET /actuator/health` | PASS, rebuilt jar returned `UP` against Docker MySQL/Redis |
| Redis connectivity | `docker compose -f infrastructure/docker-compose.yml exec -T redis redis-cli ping` | PASS, `PONG` |
| MinIO connectivity | `GET /minio/health/live` on local MinIO | PASS |

## Coverage Notes

- Backend tests cover authentication, authorization, company/licence/catalogue, complaint, investigation, administrative action, privacy redaction, hotspot/SLA/Trust Score and the new Phase 8 rate limiter.
- Backend observability includes structured JSON-style logs, health probes and local Micrometer error metrics at `aaharrakshak.http.server.errors`.
- ASP.NET tests cover portal model/client behavior from earlier phases.
- Android tests cover mobile validation and the lab-confirmation safety disclaimer.
- Android APK SHA-256: `d06d5e581e6720f95d428a86e97b20ae0c70390937a84e30a8683356f013f12e`.
- Non-blocking toolchain note: Gradle reports deprecations from the Android/Gradle toolchain for future Gradle 10 compatibility; application lint/tests/build pass.
- Container note: the first portal image build exposed an `adduser` incompatibility in the .NET runtime image; `official-web-dotnet/Dockerfile` now uses the built-in `$APP_UID` non-root user and the rebuild passes.

## Live Workflow Verification

Verified against the Spring Boot API running on `localhost:8080`, Docker MySQL, Docker Redis and Docker MinIO.

| Step | Result |
| --- | --- |
| Citizen login, draft and submit | PASS, ticket `ARK-20260723-044072` reached `SUBMITTED` |
| District officer assignment | PASS, complaint reached `ASSIGNED` |
| Inspector schedule, check-in and visit record | PASS, visit reached `COMPLETED` |
| Sample collection and custody | PASS, sample `SMP-20260723-928420` created |
| Lab assignment and receipt | PASS, sample reached `RECEIVED` |
| Lab report draft, submit, senior review and publish | PASS, report `LAB-P8-1784808692` reached `PUBLISHED` with `SAFE` outcome |
| Public complaint tracking | PASS, public complaint status returned `REPORT_PUBLISHED` |
| Public report privacy redaction | PASS, public report contains company/product/lab summary and no citizen name, email or mobile fields |

## Remaining Manual Demonstration Items

- Install the generated debug APK on a local Android device or emulator and walk through the citizen UI.
- Present the five-minute demo script in `docs/demo-script.md`.
