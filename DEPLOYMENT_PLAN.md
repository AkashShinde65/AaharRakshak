# AaharRakshak Public Deployment Plan

Prepared on: 2026-07-23.

Status: planning only. No provider login, repository push, resource creation, public deployment, paid purchase or domain purchase has been performed.

## Selected Free Demo Architecture

The best no-GitHub-Student public demo path is:

| Layer | Recommended provider | Free URL / endpoint format | Reason |
| --- | --- | --- | --- |
| Spring Boot API | Render Free Docker Web Service | `https://aaharrakshak-api.onrender.com` | Supports Docker web services, managed HTTPS/TLS and provider subdomains. |
| ASP.NET Core MVC portal | Render Free Docker Web Service | `https://aaharrakshak-portal.onrender.com` | Keeps the portal separate from backend business rules and uses the same HTTPS/subdomain model. |
| Redis-compatible Pub/Sub | Render Free Key Value | Internal `redis://red-...:6379` | Same Render private network, no external Redis exposure needed, and no password/TLS code change required if internal auth remains disabled. |
| MySQL-compatible database | TiDB Cloud Starter | TiDB MySQL endpoint, usually port `4000` | MySQL protocol compatibility with a larger free database tier than short-lived hobby databases. Must verify Flyway V1-V7 before public demo. |
| Image/video/PDF object storage | Cloudflare R2 Free | `https://<ACCOUNT_ID>.r2.cloudflarestorage.com` | S3-compatible object storage with a useful free tier and no egress fees. Current backend stores metadata/mock object keys, so real binary upload needs an approved storage-adapter enhancement before using R2 for actual evidence. |
| HTTPS | Render managed TLS | Included on `onrender.com` services | Avoids certificate setup for the academic demo. |
| Provider subdomain | Render | `*.onrender.com` | No custom domain purchase required. |

If the exact Render service names are taken, Render may generate a slightly different `onrender.com` hostname. Update `PUBLIC_BASE_URL`, `CORS_ALLOWED_ORIGINS`, portal `AaharRakshakApi__BaseUrl`, and Android `AAHAR_API_BASE_URL` to the actual hostnames.

This is a public demo architecture, not a production safety-system architecture.

## Project Inspection Summary

The deployment plan was prepared after reviewing the project brief, agent rules, final audit documents, final demo results, architecture/security/API docs, deployment docs, user manuals, and current runtime configuration.

Important implementation/configuration files:

| Area | File | Notes |
| --- | --- | --- |
| Backend Docker image | `backend-spring/Dockerfile` | Java 21 multi-stage build, non-root runtime user, exposes 8080. Render will override the app port through `SERVER_PORT=10000`. |
| Portal Docker image | `official-web-dotnet/Dockerfile` | .NET 10 publish image, ASP.NET runtime image, non-root user, `ASPNETCORE_URLS` override supported. |
| Local full stack | `infrastructure/docker-compose.yml` | Local MySQL 8.4, Redis 7, MinIO, Spring API and ASP.NET portal with health checks for MySQL/Redis/MinIO. |
| Backend production profile | `backend-spring/src/main/resources/application-prod.yml` | Requires DB/JWT/CORS env vars, enables SSL JDBC, uses schema validation and forward headers. |
| Backend base config | `backend-spring/src/main/resources/application.yml` | `SERVER_PORT`, Redis, JWT lifetimes, CORS, rate limit, upload limits and hotspot/SLA settings are env-driven. |
| Portal production config | `official-web-dotnet/appsettings.Production.json` | API base URL is environment-overridable with `AaharRakshakApi__BaseUrl`. |
| Portal security | `official-web-dotnet/Program.cs` | Cookie auth, anti-forgery, role policies, `/health`, HSTS outside development and secure headers. |
| Android API URL | `android-app/app/build.gradle.kts` | `AAHAR_API_BASE_URL` Gradle property controls the API base URL. |
| Android HTTPS policy | `android-app/app/src/main/res/xml/network_security_config.xml` | Cleartext is allowed only for emulator/local development; HTTPS provider URLs are allowed by base policy. |
| Ignore rules | `.gitignore` | Excludes `.env`, `*.env`, keystores, `google-services.json`, `android-app/local.properties`, APK/AAB outputs and build directories. |

Final release evidence already on record:

- `FINAL_TEST_REPORT.md`: backend, portal, Android build/lint/unit tests, Docker Compose, MySQL/Redis/MinIO health, Flyway V1-V7 and end-to-end workflow passed locally.
- `DEMO_RESULTS.md`: final rehearsal passed API, portal, privacy checks, recall, hotspot and SLA flows; Android device/emulator execution was not tested because `adb` was unavailable.
- `PROJECT_STATUS.md`: Phase 8 and final release audit are complete; public deployment remains NOT TESTED.

## Provider Research And Free-Tier Limits

All provider notes below come from official provider documentation researched on 2026-07-23.

| Provider | Official source | Useful free capability | Limits and risk |
| --- | --- | --- | --- |
| Render | [Deploy for Free](https://render.com/docs/free), [Web Services](https://render.com/docs/web-services), [Blueprint Spec](https://render.com/docs/blueprint-spec), [Key Value](https://render.com/docs/key-value) | Free web services, managed TLS, `onrender.com` subdomains, Docker builds, free Key Value. | Web services spin down after 15 minutes idle and cold-start on next request. Free web service filesystem is ephemeral. Free workspace gets 750 instance-hours/month; two always-awake services can exhaust this. Bandwidth/build minute overages can suspend services or bill if payment is present. Free Key Value has no persistence and loses data on restart. |
| TiDB Cloud | [Pricing](https://www.pingcap.com/pricing/), [MySQL Compatibility](https://docs.pingcap.com/tidbcloud/mysql-compatibility/) | Starter tier from $0 with MySQL protocol compatibility, 25 GiB row storage, 25 GiB column storage and 250M request units/month. | It is MySQL-compatible, not the MySQL engine. Stored procedures, triggers, events, UDFs and some MySQL features are unsupported. Usage beyond free allowances may be billable unless spend limits prevent it. Flyway V1-V7 must be tested against the actual TiDB cluster. |
| Cloudflare R2 | [Pricing](https://developers.cloudflare.com/r2/pricing/), [R2 API](https://developers.cloudflare.com/r2/api/), [R2 Tokens](https://developers.cloudflare.com/r2/api/tokens/) | S3-compatible object storage, 10 GB-month Standard storage, 1M Class A operations/month, 10M Class B operations/month and no egress fees in the free tier. | Operation/storage overages are billable. R2 must be enabled before API tokens can be generated. Current backend only stores object metadata/mock keys; do not claim real binary evidence storage on R2 until an approved adapter is implemented and tested. |
| Koyeb | [Instances](https://www.koyeb.com/docs/reference/instances), [Scale to Zero](https://www.koyeb.com/docs/run-and-scale/scale-to-zero) | One free instance per organization with 512 MB RAM, 0.1 vCPU and provider domains. | Only one free instance, no volumes, limited regions, and free services scale to zero after 1 hour idle. Not enough for separate API, portal, Redis and storage without paid services. |
| Railway | [Pricing](https://railway.com/pricing) | Docker-oriented platform with service domains and a free trial/limited free plan. | Free starts with a 30-day $5 trial, then small monthly credit. Free resources are constrained; after trial, projects/services/storage/custom-domain allowances are limited and usage can consume credits. Better for a short trial than a stable no-cost public demo. |
| Fly.io | [Pricing](https://fly.io/docs/about/pricing/) | Strong container hosting. | New users should not assume a general free plan; new organizations require a credit card. Not selected for a no-purchase/no-paid-resource demo. |
| Google Cloud Run | [Pricing](https://cloud.google.com/run/pricing) | Has monthly free compute/request allowances and HTTPS `run.app` URLs. | Requires a billing account and separate managed database/cache/storage choices. Easy to create chargeable resources accidentally. Not selected for the first free demo. |
| Oracle Cloud Free Tier | [Free Tier FAQ](https://www.oracle.com/cloud/free/faq/), [Always Free Resources](https://docs.oracle.com/en-us/iaas/Content/FreeTier/freetier_topic-Always_Free_Resources.htm), [Always Free MySQL HeatWave](https://docs.oracle.com/en-us/iaas/mysql-database/doc/creating-always-free-db-system.html) | Always Free compute, object storage and MySQL HeatWave can host a fuller stack with less sleeping. | Requires account signup with card/identity verification, regional capacity can be constrained, no simple application provider subdomain, and more ops work for TLS/reverse proxy. Strong fallback if Render/TiDB limits block the demo. |

## Why This Architecture Was Chosen

Render is the selected public web platform because it directly satisfies the visible-demo requirements: Docker deploys, managed HTTPS, public provider subdomains and no GitHub Student dependency.

TiDB Cloud is selected for the database because the application is MySQL/Flyway-based and Render's free managed relational database is PostgreSQL, not MySQL, and expires after 30 days. TiDB Cloud Starter is a MySQL-compatible free option with meaningful storage, but it must still be migration-tested because it is not identical to MySQL 8.

Render Key Value is selected for Redis because it can be attached through Render's internal Redis-compatible URL. It is not persistent on the free plan, but the AaharRakshak alert outbox remains durable in MySQL/TiDB, so Redis is used for real-time fan-out only.

Cloudflare R2 is selected for future evidence/report binary storage because it is S3-compatible and has a practical free tier. The current backend deliberately stores metadata and object keys for academic demo flows; actual R2 upload/download must be added and tested before uploading real evidence files.

## Free-Tier Risk Notes

- Sleeping: Render free API and portal services spin down after 15 minutes with no inbound traffic. First request after idle can take about one minute.
- Service hours: Render gives 750 free instance-hours per workspace per month. Keeping both API and portal awake continuously can exceed this.
- Filesystem loss: Render free web service local files are lost after redeploy, restart or spin-down. Never store evidence, PDFs or database files on the service filesystem.
- Redis loss: Render Free Key Value has no persistence. Treat it only as transient alert fan-out. Keep alert state in `alert_outbox`.
- Database compatibility: TiDB Cloud is MySQL-compatible but not exact MySQL. Avoid unsupported MySQL features and validate Flyway V1-V7 on the target.
- Storage charges: R2 has a free tier, but storage and operation overages can be billable. Keep buckets private and use quotas/spend alerts where available.
- External traffic: Public demo traffic can consume bandwidth, build minutes, requests, Redis commands and R2 operations.
- No SLA: Free tiers are suitable for academic demonstration, not public-health production workloads.

## Existing Production Dockerfiles

No Dockerfile rewrite is required before planning approval:

- API: `backend-spring/Dockerfile`
- Portal: `official-web-dotnet/Dockerfile`

Render will build each service from its own Docker context. Because Render expects web apps on port `10000` by default, the deployment manifest sets:

- API: `SERVER_PORT=10000`
- Portal: `ASPNETCORE_URLS=http://+:10000`

The Dockerfiles still expose `8080` for local Docker Compose. That is acceptable because `EXPOSE` is documentation for the image, while the app bind port is controlled by runtime env vars.

## Deployment Manifest

The local draft Render blueprint is:

- `render.yaml`

It defines:

- `aaharrakshak-api`: Docker web service for Spring Boot.
- `aaharrakshak-portal`: Docker web service for ASP.NET Core MVC.
- `aaharrakshak-redis`: Render Free Key Value, public external access blocked with an empty `ipAllowList`.

The manifest uses `sync: false` for external database secrets and `generateValue: true` for the JWT secret. It does not contain real credentials.

## Environment Variables

### Spring Boot API

Required for Render:

| Variable | Example value | Secret? | Notes |
| --- | --- | --- | --- |
| `SPRING_PROFILES_ACTIVE` | `prod` | No | Uses production config. |
| `SERVER_PORT` | `10000` | No | Render web-service port. |
| `PUBLIC_BASE_URL` | `https://aaharrakshak-api.onrender.com` | No | Update if Render generated hostname differs. |
| `CORS_ALLOWED_ORIGINS` | `https://aaharrakshak-portal.onrender.com` | No | Exact origins only. Add Android deep links only if introduced later. |
| `DB_HOST` | `<tidb-host>` | Treat as sensitive | Store only in Render env. |
| `DB_PORT` | `4000` | No | TiDB Cloud commonly uses 4000; use provider value. |
| `DB_NAME` | `aaharrakshak` | No | Create this database before deploy. |
| `DB_USERNAME` | `<tidb-user>` | Yes | Store only in Render env. |
| `DB_PASSWORD` | `<tidb-password>` | Yes | Store only in Render env. |
| `JWT_SECRET` | generated by Render | Yes | Minimum 64 random chars if supplied manually. |
| `SPRING_DATA_REDIS_URL` | from Render Key Value | Treat as sensitive | Populated from Render service connection string. Keep internal auth disabled unless app config is updated for auth. |
| `MAX_FILE_SIZE` | `10MB` | No | Matches current validators. |
| `MAX_REQUEST_SIZE` | `25MB` | No | Matches current validators. |
| `RATE_LIMIT_ENABLED` | `true` | No | Keep enabled for public demo. |
| `RATE_LIMIT_MAX_REQUESTS` | `120` | No | Tune only after testing. |
| `RATE_LIMIT_WINDOW_SECONDS` | `60` | No | Tune only after testing. |
| `ERROR_METRICS_ENABLED` | `true` | No | Local Micrometer metrics only. |
| `ACCESS_TOKEN_MINUTES` | `20` | No | Optional override. |
| `REFRESH_TOKEN_DAYS` | `7` | No | Optional override. |
| `MAX_LOGIN_ATTEMPTS` | `5` | No | Optional override. |
| `LOCK_MINUTES` | `15` | No | Optional override. |
| `HOTSPOT_RADIUS_KM` | `2.0` | No | Optional override. |
| `HOTSPOT_WINDOW_HOURS` | `24` | No | Optional override. |
| `HOTSPOT_CRITICAL_THRESHOLD` | `10` | No | Optional override. |
| `HIGH_RISK_THRESHOLD` | `70` | No | Optional override. |
| `HIGH_RISK_SLA_HOURS` | `48` | No | Optional override. |

Future R2 variables before real binary storage:

| Variable | Example value | Secret? | Status |
| --- | --- | --- | --- |
| `STORAGE_PROVIDER` | `s3` | No | Not consumed by current backend. |
| `S3_ENDPOINT` | `https://<ACCOUNT_ID>.r2.cloudflarestorage.com` | No | Not consumed by current backend. |
| `S3_REGION` | `auto` | No | Not consumed by current backend. |
| `S3_BUCKET` | `aaharrakshak-evidence` | No | Not consumed by current backend. |
| `S3_ACCESS_KEY_ID` | `<r2-access-key>` | Yes | Not consumed by current backend. |
| `S3_SECRET_ACCESS_KEY` | `<r2-secret-key>` | Yes | Not consumed by current backend. |
| `S3_PATH_STYLE_ACCESS` | `true` | No | Not consumed by current backend. |

Do not add the R2 secret values to the repository. Add them only to the hosting provider secret manager after the storage adapter is approved and implemented.

### ASP.NET Core MVC Portal

| Variable | Example value | Secret? | Notes |
| --- | --- | --- | --- |
| `ASPNETCORE_ENVIRONMENT` | `Production` | No | Enables production pipeline. |
| `ASPNETCORE_URLS` | `http://+:10000` | No | Render web-service port. |
| `AaharRakshakApi__BaseUrl` | `https://aaharrakshak-api.onrender.com` | No | Portal calls the public API URL. |
| `ASPNETCORE_FORWARDEDHEADERS_ENABLED` | `true` | No | Recommended behind a managed proxy. Verify redirect behavior after deployment. |

### Android

For a public demo build:

```bash
cd android-app
gradle test lint assembleDebug -PAAHAR_API_BASE_URL=https://aaharrakshak-api.onrender.com
```

The resulting debug APK path remains:

```text
android-app/app/build/outputs/apk/debug/app-debug.apk
```

For a production Play Store style release, create a separate release signing process and keep the keystore outside source control. Do not commit `*.keystore`, `*.jks`, `*.p12`, `*.pfx`, `google-services.json`, `.env`, or `android-app/local.properties`.

## Health Checks

| Component | Check |
| --- | --- |
| API service | `GET https://aaharrakshak-api.onrender.com/api/v1/health` should return HTTP 200 and service status `UP`. |
| API actuator | `GET https://aaharrakshak-api.onrender.com/actuator/health` should return HTTP 200. |
| Portal service | `GET https://aaharrakshak-portal.onrender.com/health` should return HTTP 200. |
| MySQL/TiDB | Query `flyway_schema_history`; latest applied version must be `7`. |
| Redis | From Render service shell or logs, verify `SPRING_DATA_REDIS_URL` connection and alert publication without exceptions. |
| Object storage | Before real uploads, verify private bucket, least-privilege token and a `head-bucket` or signed upload/download test. |
| Privacy smoke test | Public report/search responses must not contain citizen name, email, phone, exact private citizen GPS, seal numbers or chain-of-custody internals. |

## Exact Deployment Steps After Approval

Stop here until the user approves external actions. After approval:

1. Create or select accounts:
   - Render account for API, portal and Redis-compatible Key Value.
   - TiDB Cloud account for MySQL-compatible database.
   - Cloudflare account only if real R2 binary evidence/report storage is approved.
   - Optional domain registrar account only if `aaharrakshak.in` is later purchased.

2. Prepare source hygiene locally:
   - Confirm no real `.env`, key, keystore, APK/AAB release artifact or credential file is staged.
   - Because this folder is not currently a Git repository, initialize/clone the future repo first, then run `git status --short --ignored` and verify ignore behavior before pushing.
   - Keep `android-app/local.properties` local only.

3. Re-run local release checks before pushing:

```bash
cd backend-spring
mvn clean verify
cd ..
dotnet build official-web-dotnet/official-web-dotnet.csproj
dotnet test official-web-dotnet.Tests/official-web-dotnet.Tests.csproj
cd android-app
gradle test lint assembleDebug -PAAHAR_API_BASE_URL=https://aaharrakshak-api.onrender.com
cd ..
docker compose -f infrastructure/docker-compose.yml config
```

4. Create the TiDB Cloud Starter cluster:
   - Create database `aaharrakshak`.
   - Create an application user with only required privileges.
   - Copy host, port, username and password into a password manager.
   - Do not commit them.

5. Validate MySQL compatibility before public traffic:
   - Set backend env vars for TiDB.
   - Start the API once and confirm Flyway applies V1-V7.
   - Query `flyway_schema_history` and confirm version `7`.

6. Create the Render Blueprint from `render.yaml`:
   - Connect the approved repository.
   - Confirm services: `aaharrakshak-api`, `aaharrakshak-portal`, `aaharrakshak-redis`.
   - Fill `sync: false` DB env vars in the Render Dashboard.
   - Leave Render Key Value external access blocked.
   - Keep auto deploy disabled until manual smoke tests pass.

7. Update final hostnames:
   - Set API `PUBLIC_BASE_URL` to the actual API `onrender.com` URL.
   - Set API `CORS_ALLOWED_ORIGINS` to the actual portal `onrender.com` URL.
   - Set portal `AaharRakshakApi__BaseUrl` to the actual API URL.
   - Rebuild Android with the actual API URL.

8. Run public smoke tests:

```bash
curl -fsS https://aaharrakshak-api.onrender.com/api/v1/health
curl -fsS https://aaharrakshak-api.onrender.com/actuator/health
curl -fsS https://aaharrakshak-api.onrender.com/v3/api-docs
curl -fsS https://aaharrakshak-portal.onrender.com/health
```

9. Run a minimal public demo workflow with mock data only:
   - Login as seeded citizen/company/official roles.
   - Submit a packaged-food complaint using demo barcode `8901234567890`.
   - Submit a prepared-dish complaint.
   - Assign, inspect, collect sample, publish mock lab report.
   - Issue show-cause notice, company response, simulated decision.
   - Confirm public report is anonymized.
   - Confirm recall/alert/hotspot/SLA endpoints work.

10. Build Android against the public API URL:

```bash
cd android-app
gradle test lint assembleDebug -PAAHAR_API_BASE_URL=https://aaharrakshak-api.onrender.com
```

11. Install on an emulator/device only after the public API health check passes:

```bash
adb install -r android-app/app/build/outputs/apk/debug/app-debug.apk
adb shell monkey -p com.aaharrakshak.mobile 1
```

## Optional Paid Custom Domain: aaharrakshak.in

Do not purchase `aaharrakshak.in` without explicit permission.

If purchased later:

1. Buy/control `aaharrakshak.in` through a registrar.
2. Add DNS records:
   - `api.aaharrakshak.in` -> Render API custom-domain target.
   - `portal.aaharrakshak.in` -> Render portal custom-domain target.
3. Enable/verify Render managed TLS for both hostnames.
4. Update:
   - `PUBLIC_BASE_URL=https://api.aaharrakshak.in`
   - `CORS_ALLOWED_ORIGINS=https://portal.aaharrakshak.in`
   - `AaharRakshakApi__BaseUrl=https://api.aaharrakshak.in`
   - Android `-PAAHAR_API_BASE_URL=https://api.aaharrakshak.in`
5. Keep registrar login, DNS tokens and any payment information outside this repository.

## Upload-Safety Check

Current local checks found:

- No `.env`, `*.env`, keystore, `.jks`, `.p12`, `.pfx`, or `google-services.json` file in the source tree searched outside ignored build output.
- `android-app/local.properties` exists and is local-only; `.gitignore` excludes it.
- `.gitignore` excludes `.env`, `*.env`, build outputs, Android build output, APK/AAB files, keystores, `google-services.json`, logs and `AaharRakshak-source.zip`.
- Secret-pattern scan hits were expected demo placeholders, local demo passwords, tests and documentation. No real API key, private key, JWT signing secret or production credential was identified.
- The workspace is not currently a Git repository, so `git check-ignore` could not be used. Re-run Git-based staging/ignore checks before any future push.

Never upload:

- Real passwords, JWT secrets, database credentials or R2 keys.
- `.env` files.
- Android keystores or signing configs.
- `android-app/local.properties`.
- Real Aadhaar data, Aadhaar images, biometrics, unauthorized government data, real SMS provider keys or paid service keys.
- `target`, `bin`, `obj`, `.gradle`, APK/AAB release artifacts or temporary build files.

## Public Demo Boundaries

- Use mock demonstration data only.
- Do not integrate real Aadhaar.
- Do not scrape or call unauthorized government data.
- Do not perform real licence suspension/cancellation.
- Do not send real SMS/push/email beyond mock adapters.
- Do not claim image, OCR or AI output proves chemical adulteration without laboratory confirmation.
- Do not expose citizen identity to companies or public users.
- Do not deploy, create accounts/resources or purchase a domain without explicit user approval.
