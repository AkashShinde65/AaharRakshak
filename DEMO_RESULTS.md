# AaharRakshak Final Demo Results

Demo rehearsal date: 2026-07-23.

Overall result: PASS for API, portal, mock-data workflow, privacy checks, recall, hotspot and SLA rehearsal. Android installation/opening was NOT TESTED because `adb` was not available in this environment.

## Environment

| Item | Result |
| --- | --- |
| Database schema | `aaharrakshak_demo_rehearsal` |
| Mock data source | Flyway V1-V7 seed/demo migrations only |
| Spring Boot API | Started on `http://localhost:8080`, health PASS |
| ASP.NET Core MVC portal | Started on `http://localhost:5080`, public and authenticated portal checks PASS |
| Docker services | MySQL, Redis and MinIO healthy before shutdown |
| Shutdown | API stopped cleanly, portal stopped cleanly, Docker MySQL/Redis/MinIO stopped, ports clear |
| Android APK install/open | NOT TESTED, `adb` command not found |

## Commands And Results

| Command | Result |
| --- | --- |
| `sed -n '1,220p' FINAL_TEST_REPORT.md` | PASS |
| `sed -n '1,180p' RELEASE_CHECKLIST.md` | PASS |
| `sed -n '1,180p' PROJECT_STATUS.md` | PASS |
| `which adb; adb devices` | NOT TESTED, `adb` not installed |
| `docker compose -f infrastructure/docker-compose.yml up -d mysql redis minio` | PASS |
| `docker compose -f infrastructure/docker-compose.yml ps` | PASS, MySQL/Redis/MinIO healthy before shutdown |
| `docker compose -f infrastructure/docker-compose.yml exec -T redis redis-cli ping` | PASS, `PONG` |
| `docker compose -f infrastructure/docker-compose.yml exec -T minio curl -fsS http://localhost:9000/minio/health/live` | PASS |
| `docker compose -f infrastructure/docker-compose.yml exec -T mysql mysql -uroot -proot_password -e "<reset demo schema>"` | PASS |
| `SPRING_PROFILES_ACTIVE=docker ... java -jar target/backend-spring-0.0.1-SNAPSHOT.jar` | PASS, API started and Flyway migrated V1-V7 |
| `AaharRakshakApi__BaseUrl=http://localhost:8080 dotnet run --project official-web-dotnet/official-web-dotnet.csproj --urls http://localhost:5080` | PASS, portal started |
| Demo workflow HTTP script | PASS after correcting demo evidence timestamps |
| Portal public/authenticated page script | PASS |
| MySQL Flyway/audit evidence query | PASS for Flyway, complaints, audit logs and lab report; first action query used wrong column name and was rerun |
| `DESCRIBE actions; SELECT * FROM actions WHERE action_number='ADM-20260723-058999'\G` | PASS |
| `docker compose -f infrastructure/docker-compose.yml stop mysql redis minio` | PASS |
| `lsof` checks for ports 8080, 5080, 3306, 6379, 9000 and 9001 | PASS, no listening services |

No source code changes were required. The first rehearsal payload used future evidence `capturedAt` timestamps and was rejected; the affected workflow was rerun with `2026-07-20T10:00:00Z`, which is valid for the July 23, 2026 rehearsal date.

## Demo Credentials Used

| Role | Credential | Result |
| --- | --- | --- |
| Citizen | `citizen@aaharrakshak.dev` / `password` | PASS |
| Company | `company@aaharrakshak.dev` / `password` | PASS |
| Food Inspector | `inspector@aaharrakshak.dev` / `password` | PASS |
| Laboratory Officer | `lab@aaharrakshak.dev` / `password` | PASS |
| District Officer | `district@aaharrakshak.dev` / `password` | PASS |
| Central Administrator | `admin@aaharrakshak.dev` / `password` | PASS |

## Rehearsal IDs

| Item | Value |
| --- | --- |
| Packaged-food complaint | `ARK-20260723-606147` |
| Prepared-dish complaint | `ARK-20260723-807643` |
| Inspection | `3` |
| Sample ID | `3` |
| Sample number | `SMP-20260723-991064` |
| Lab report ID | `2` |
| Lab report number | `LAB-DEMO-20260723125926` |
| Show-cause notice | `SCN-20260723-661987` |
| Simulated administrative action | `ADM-20260723-058999` |

## API URLs Tested

| Step | URL | HTTP | Result |
| --- | --- | --- | --- |
| API health | `GET http://localhost:8080/api/v1/health` | 200 | PASS |
| Citizen login | `POST http://localhost:8080/api/v1/auth/login` | 200 | PASS |
| Company login | `POST http://localhost:8080/api/v1/auth/login` | 200 | PASS |
| Inspector login | `POST http://localhost:8080/api/v1/auth/login` | 200 | PASS |
| Lab login | `POST http://localhost:8080/api/v1/auth/login` | 200 | PASS |
| District login | `POST http://localhost:8080/api/v1/auth/login` | 200 | PASS |
| Admin login | `POST http://localhost:8080/api/v1/auth/login` | 200 | PASS |
| Public barcode lookup | `GET http://localhost:8080/api/v1/public/products/barcodes/8901234567890` | 200 | PASS |
| Citizen packaged-food scan | `POST http://localhost:8080/api/v1/citizen/scans/packaged-food` | 200 | PASS |
| Packaged complaint draft | `POST http://localhost:8080/api/v1/citizen/complaints/drafts` | 201 | PASS |
| Packaged complaint submit | `POST http://localhost:8080/api/v1/citizen/complaints/21/submit` | 200 | PASS |
| Prepared-dish complaint draft | `POST http://localhost:8080/api/v1/citizen/complaints/drafts` | 201 | PASS |
| Prepared-dish complaint submit | `POST http://localhost:8080/api/v1/citizen/complaints/22/submit` | 200 | PASS |
| Company denied official complaint | `GET http://localhost:8080/api/v1/official/complaints/ARK-20260723-606147` | 403 | PASS |
| Lab denied unassigned complaint | `GET http://localhost:8080/api/v1/official/investigations/complaints/ARK-20260723-606147` | 403 | PASS |
| Official dashboard | `GET http://localhost:8080/api/v1/official/investigations/dashboard` | 200 | PASS |
| Assign inspector | `POST http://localhost:8080/api/v1/official/investigations/complaints/ARK-20260723-606147/assign-inspector` | 200 | PASS |
| Inspector complaint details | `GET http://localhost:8080/api/v1/official/investigations/complaints/ARK-20260723-606147` | 200 | PASS |
| Schedule inspection | `POST http://localhost:8080/api/v1/official/investigations/complaints/ARK-20260723-606147/inspections/schedule` | 200 | PASS |
| Geotag check-in | `POST http://localhost:8080/api/v1/official/investigations/inspections/3/check-in` | 200 | PASS |
| Visit record | `POST http://localhost:8080/api/v1/official/investigations/inspections/3/visit-record` | 200 | PASS |
| Collect sample | `POST http://localhost:8080/api/v1/official/investigations/inspections/3/samples` | 200 | PASS |
| Assign lab | `POST http://localhost:8080/api/v1/official/investigations/samples/3/assign-lab` | 200 | PASS |
| Lab assigned samples | `GET http://localhost:8080/api/v1/lab/investigations/samples/assigned` | 200 | PASS |
| Sample received | `POST http://localhost:8080/api/v1/lab/investigations/samples/3/received` | 200 | PASS |
| Lab draft report | `POST http://localhost:8080/api/v1/lab/investigations/samples/3/reports/drafts` | 200 | PASS |
| Lab submit report | `POST http://localhost:8080/api/v1/lab/investigations/reports/2/submit` | 200 | PASS |
| District review report | `POST http://localhost:8080/api/v1/official/investigations/lab-reports/2/review` | 200 | PASS |
| District publish report | `POST http://localhost:8080/api/v1/official/investigations/lab-reports/2/publish` | 200 | PASS |
| Issue show-cause notice | `POST http://localhost:8080/api/v1/official/admin-actions/reports/2/show-cause-notices` | 200 | PASS |
| Company notice details | `GET http://localhost:8080/api/v1/company/admin-actions/notices/SCN-20260723-661987` | 200 | PASS |
| Company response | `POST http://localhost:8080/api/v1/company/admin-actions/notices/SCN-20260723-661987/responses` | 200 | PASS |
| District review response | `POST http://localhost:8080/api/v1/official/admin-actions/notices/SCN-20260723-661987/review` | 200 | PASS |
| Inspector denied action approval | `POST http://localhost:8080/api/v1/official/admin-actions/notices/SCN-20260723-661987/decision` | 403 | PASS |
| District simulated recall decision | `POST http://localhost:8080/api/v1/official/admin-actions/notices/SCN-20260723-661987/decision` | 200 | PASS |
| Public complaint tracking | `GET http://localhost:8080/api/v1/public/transparency/complaints/ARK-20260723-606147/status` | 200 | PASS |
| Public anonymized report | `GET http://localhost:8080/api/v1/public/transparency/reports/LAB-DEMO-20260723125926` | 200 | PASS |
| Public transparency search | `GET http://localhost:8080/api/v1/public/transparency/search?product=turmeric` | 200 | PASS |
| Public recalls | `GET http://localhost:8080/api/v1/public/transparency/recalls` | 200 | PASS |
| Public safety alerts | `GET http://localhost:8080/api/v1/public/transparency/alerts` | 200 | PASS |
| Citizen alerts | `GET http://localhost:8080/api/v1/citizen/alerts` | 200 | PASS |
| District hotspot dashboard | `GET http://localhost:8080/api/v1/official/intelligence/hotspots/district?district=Pune` | 200 | PASS |
| Run hotspot detection | `POST http://localhost:8080/api/v1/official/intelligence/hotspots/detect` | 200 | PASS |
| Mock risk analysis | `POST http://localhost:8080/api/v1/official/intelligence/risk/complaints/ARK-HOT-0001` | 200 | PASS |
| Inspector denied high-risk close | `POST http://localhost:8080/api/v1/official/intelligence/complaints/ARK-SLA-0007/close` | 403 | PASS |
| SLA overdue escalation | `POST http://localhost:8080/api/v1/official/intelligence/sla/check-overdue` | 200 | PASS |
| SLA escalations | `GET http://localhost:8080/api/v1/official/intelligence/sla/escalations` | 200 | PASS |
| Alert outbox | `GET http://localhost:8080/api/v1/official/intelligence/alerts/outbox` | 200 | PASS |

## Portal URLs Tested

| Step | URL | HTTP | Result |
| --- | --- | --- | --- |
| Portal root redirect | `GET http://localhost:5080/` | 302 | PASS |
| Public reports page | `GET http://localhost:5080/Public/Reports` | 200 | PASS |
| Login page | `GET http://localhost:5080/Auth/Login` | 200 | PASS |
| Official dashboard requires login | `GET http://localhost:5080/Official/Dashboard` | 302 | PASS |
| Public complaint page | `GET http://localhost:5080/Public/Reports/Complaint/ARK-20260723-606147` | 200 | PASS |
| Public report page | `GET http://localhost:5080/Public/Reports/Report/LAB-DEMO-20260723125926` | 200 | PASS |
| Public alerts page | `GET http://localhost:5080/Public/Reports/Alerts` | 200 | PASS |
| Public batch page | `GET http://localhost:5080/Public/Reports/Batch?batchNumber=TUR-2026-001` | 200 | PASS |
| Public licence page | `GET http://localhost:5080/Public/Reports/Licence?licenceNumber=12345678901234` | 200 | PASS |
| Public Trust Score page | `GET http://localhost:5080/Public/Reports/Trust?CompanyId=1` | 200 | PASS |
| Portal login as district officer | `POST http://localhost:5080/Auth/Login` | 302 | PASS |
| Official dashboard | `GET http://localhost:5080/Official/Dashboard` | 200 | PASS |
| Official hotspot dashboard | `GET http://localhost:5080/Official/Dashboard/Hotspots?district=Pune` | 200 | PASS |
| Official escalations dashboard | `GET http://localhost:5080/Official/Dashboard/Escalations` | 200 | PASS |
| Official alerts dashboard | `GET http://localhost:5080/Official/Dashboard/Alerts` | 200 | PASS |
| Portal login as company | `POST http://localhost:5080/Auth/Login` | 302 | PASS |
| Company dashboard | `GET http://localhost:5080/Company/Dashboard` | 200 | PASS |
| Company notice page | `GET http://localhost:5080/Company/Dashboard/Notice/SCN-20260723-661987` | 200 | PASS |

## Privacy And Safety Checks

| Check | Result |
| --- | --- |
| Company cannot access citizen complaint through official API | PASS, HTTP 403 |
| Lab officer cannot access unassigned inspector complaint | PASS, HTTP 403 |
| Inspector-assigned complaint response did not contain citizen email/mobile/demo citizen text | PASS |
| Company notice details did not contain citizen email/mobile/demo citizen text | PASS |
| Public report did not contain citizen email/mobile/demo citizen text, seal number or chain-of-custody internals | PASS |
| Public search did not expose citizen identity or latitude/longitude | PASS |
| Scan and risk analysis safety notes did not claim image-only adulteration proof | PASS |
| Administrative action was simulated | PASS, `simulated=1` |

## Database Evidence

| Evidence | Result |
| --- | --- |
| Flyway migration version | PASS, V1-V7 applied successfully |
| Complaint count after rehearsal | 16 |
| Audit log count after rehearsal | 36 |
| Packaged complaint | `ARK-20260723-606147`, `ACTION_TAKEN`, `PACKAGED_FOOD` |
| Dish complaint | `ARK-20260723-807643`, `SUBMITTED`, `PREPARED_DISH` |
| Lab report | `LAB-DEMO-20260723125926`, `PUBLISHED`, `ADULTERATED` |
| Administrative action | `ADM-20260723-058999`, `BATCH_RECALL`, simulated |

Audit-log actions recorded included complaint draft/submission, assignment, inspection scheduling/check-in/visit, sample collection, lab assignment/receipt/report draft/submission/review/publication, show-cause notice, company response, administrative action approval, hotspot detection and SLA escalation.

## Android Status

| Item | Result |
| --- | --- |
| APK path | `/Users/akashashokshinde/Desktop/Aaharrakshak/android-app/app/build/outputs/apk/debug/app-debug.apk` |
| APK file present | PASS |
| APK install/open | NOT TESTED |
| Reason | `adb` was not installed: `zsh:1: command not found: adb` |
| Exact install/open steps | Documented in `DEMO_RUNBOOK.md` |

## Final Presentation Order

1. System health and mock-only boundaries.
2. Citizen login, barcode lookup and packaged-food scan.
3. Packaged complaint with image/video/receipt and GPS evidence.
4. Prepared-dish complaint with unknown vendor, dish/vendor images and GPS consent.
5. District assignment and inspector visit/check-in.
6. Sample collection and chain-of-custody.
7. Lab receipt, draft report, submit, review and publish.
8. Show-cause notice, company response and simulated `BATCH_RECALL`.
9. Public anonymized report, recall alert, hotspot dashboard and SLA escalation.
10. Android APK path and install steps; state clearly that device/emulator execution was not tested in this rehearsal.
