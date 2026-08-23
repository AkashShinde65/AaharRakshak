# AaharRakshak Release Checklist

Audit date: 2026-07-23.

## Build And Test Gates

| Gate | Result |
| --- | --- |
| Project brief, `AGENTS.md` and `PROJECT_STATUS.md` re-read | PASS |
| No Phase 9 or new feature work started | PASS |
| `mvn clean verify` | PASS |
| `dotnet build` | PASS |
| `dotnet test` | PASS |
| Android unit tests | PASS |
| Android lint | PASS |
| Android debug APK build | PASS |
| Docker Compose config validation | PASS |
| MySQL health check | PASS |
| Redis health check | PASS |
| MinIO health check | PASS |
| Flyway migrations V1-V7 on fresh MySQL schema | PASS |
| Complete citizen complaint-to-public-report workflow | PASS |
| Swagger/OpenAPI available | PASS |
| Postman Phase 4-8 collections valid JSON | PASS |
| Source zip created with build/secrets exclusions | PASS |

## Functional Release Areas

| Area | Result |
| --- | --- |
| Phase 1 foundation and database baseline | PASS |
| Phase 2 authentication, security and audit logging | PASS |
| Phase 3 company, licence, product and batch management | PASS |
| Phase 4 citizen complaints and scanning | PASS |
| Phase 5 official investigation, samples and lab workflow | PASS |
| Phase 6 administrative actions, transparency APIs and ASP.NET portal | PASS |
| Phase 7 hotspots, alerts, SLA escalation, Trust Score and mock AI | PASS |
| Phase 8 Android app, deployment preparation and documentation | PASS |

## Privacy, Legal And Safety Gates

| Gate | Result |
| --- | --- |
| Full Aadhaar numbers/images/biometrics are not collected or stored | PASS |
| Mock Aadhaar verification stores only status/token | PASS |
| Unauthorized government data is not scraped or used | PASS |
| Real licence cancellation/suspension is not performed | PASS |
| Real SMS, push, payment, delivery or storefront services are not called | PASS |
| Public reports hide citizen identity, phone and email | PASS |
| Hotspots do not expose citizen GPS points publicly | PASS |
| Camera/OCR/AI warnings do not claim chemical adulteration without lab confirmation | PASS |
| Demo defaults are documented as non-production placeholders | PASS |

## Manual Or Environment-Limited Items

| Gate | Result |
| --- | --- |
| Android install and real device/emulator walkthrough | NOT TESTED |
| CameraX hardware capture behavior | NOT TESTED |
| Device GPS permission/provider behavior | NOT TESTED |
| Portal cross-browser responsive visual QA | NOT TESTED |
| Public hosting provider deployment | NOT TESTED |

## Release Decision

Release audit result: PASS for reproducible local build, automated tests, Docker service health, MySQL migrations and live backend workflow. The release remains a local academic/demo build until the NOT TESTED device, browser and deployment checks are executed in their target environments.
