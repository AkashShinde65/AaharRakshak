# AaharRakshak Public Deployment Checklist

Prepared on: 2026-07-23.

Status: planning complete; external deployment not started.

## Current Planning Status

| Item | Status | Notes |
| --- | --- | --- |
| Project documentation reviewed | PASS | Brief, `AGENTS.md`, `PROJECT_STATUS.md`, final audit docs, final demo docs, architecture/security/API/deployment docs reviewed. |
| Backend config inspected | PASS | Dockerfile, `application.yml`, `application-prod.yml`, health endpoint and env-driven settings inspected. |
| Portal config inspected | PASS | Dockerfile, production appsettings, HttpClient config, auth/cookie/security headers and `/health` inspected. |
| Android API config inspected | PASS | `AAHAR_API_BASE_URL` Gradle property and HTTPS network security policy inspected. |
| MySQL/Redis/MinIO local config inspected | PASS | `infrastructure/docker-compose.yml` and `.env.example` inspected. |
| Free hosting tiers researched | PASS | Official docs for Render, TiDB Cloud, Cloudflare R2, Koyeb, Railway, Fly.io, Google Cloud Run and Oracle Cloud reviewed. |
| Selected public demo platform | PASS | Render web services + Render Key Value + TiDB Cloud Starter + Cloudflare R2 plan selected. |
| Deployment manifest prepared | PASS | `render.yaml` added with placeholder/secret-safe env wiring. |
| External login/resource creation | NOT STARTED | Must wait for explicit user approval. |
| Public deployment | NOT STARTED | Must wait for explicit user approval. |

## Go/No-Go Gates Before External Actions

- [ ] User explicitly approves provider login/resource creation/deployment.
- [ ] Decide whether the public demo must include actual binary uploads or only the current metadata/mock object-key flow.
- [ ] Decide whether to keep Render auto-deploy disabled until manual approval after each source push.
- [ ] Confirm final service names are available or accept generated Render suffixes.
- [ ] Confirm no paid resource, domain purchase, real Aadhaar, real SMS, real government registry or real licence action is required.

## Source Hygiene

- [ ] Initialize or connect the future Git repository.
- [ ] Run `git status --short --ignored`.
- [ ] Confirm `.env`, `*.env`, `android-app/local.properties`, keystores, `google-services.json`, APK/AAB files and build outputs are not staged.
- [ ] Run a focused secret scan over source/config/docs.
- [ ] Confirm demo passwords are documented as mock-only and are not production credentials.
- [ ] Confirm no full Aadhaar numbers, Aadhaar images, biometric files or unauthorized government data are present.
- [ ] Rebuild source archive only with exclusions for secrets, `.env`, keystores, `target`, `bin`, `obj`, `.gradle` and temporary files.

## Local Verification Before Push

- [ ] `cd backend-spring`
- [ ] `mvn clean verify`
- [ ] `cd ..`
- [ ] `dotnet build official-web-dotnet/official-web-dotnet.csproj`
- [ ] `dotnet test official-web-dotnet.Tests/official-web-dotnet.Tests.csproj`
- [ ] `cd android-app`
- [ ] `gradle test lint assembleDebug -PAAHAR_API_BASE_URL=https://aaharrakshak-api.onrender.com`
- [ ] `cd ..`
- [ ] `docker compose -f infrastructure/docker-compose.yml config`
- [ ] Start local MySQL/Redis/MinIO and verify health if deployment source changed.
- [ ] Start the API and verify `GET /api/v1/health`.
- [ ] Start the portal and verify `GET /health`.

## Render Setup After Approval

- [ ] Create/select Render account.
- [ ] Connect approved repository.
- [ ] Create Blueprint from root `render.yaml`.
- [ ] Confirm `aaharrakshak-api` uses Docker context `backend-spring`.
- [ ] Confirm `aaharrakshak-portal` uses Docker context `official-web-dotnet`.
- [ ] Confirm `aaharrakshak-redis` is Render Key Value on the free plan.
- [ ] Confirm Render Key Value external access is blocked with an empty `ipAllowList`.
- [ ] Keep internal Redis authentication disabled unless the app is updated and tested for authenticated Redis URLs.
- [ ] Confirm API health path is `/api/v1/health`.
- [ ] Confirm portal health path is `/health`.
- [ ] Keep `autoDeployTrigger: off` until smoke tests pass.

## TiDB Cloud Setup After Approval

- [ ] Create/select TiDB Cloud account.
- [ ] Create Starter cluster.
- [ ] Create database `aaharrakshak`.
- [ ] Create least-privilege app user.
- [ ] Store host, port, username and password outside source control.
- [ ] Configure Render API env vars: `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`.
- [ ] Deploy API once and confirm Flyway V1-V7 apply successfully.
- [ ] Query `flyway_schema_history` and confirm latest version is `7`.
- [ ] Confirm no unsupported MySQL feature in migrations blocks TiDB.

## Cloudflare R2 Setup After Approval

- [ ] Decide whether actual binary object storage is in scope for this deployment.
- [ ] If yes, get approval to implement/test the S3/R2 storage adapter first.
- [ ] Create/select Cloudflare account.
- [ ] Enable R2 only after approval; note this may require billing setup.
- [ ] Create private bucket, for example `aaharrakshak-evidence`.
- [ ] Create a bucket-scoped Object Read & Write token.
- [ ] Store `S3_ACCESS_KEY_ID` and `S3_SECRET_ACCESS_KEY` only in provider secrets.
- [ ] Verify upload/download with checksums and private access before any demo.
- [ ] Do not make evidence/report buckets public.

## Backend Environment

- [ ] `SPRING_PROFILES_ACTIVE=prod`
- [ ] `SERVER_PORT=10000`
- [ ] `PUBLIC_BASE_URL=https://<actual-api>.onrender.com`
- [ ] `CORS_ALLOWED_ORIGINS=https://<actual-portal>.onrender.com`
- [ ] `DB_HOST=<tidb-host>`
- [ ] `DB_PORT=<tidb-port>`
- [ ] `DB_NAME=aaharrakshak`
- [ ] `DB_USERNAME=<tidb-user>`
- [ ] `DB_PASSWORD=<tidb-password>`
- [ ] `JWT_SECRET=<generated-secret>`
- [ ] `SPRING_DATA_REDIS_URL=<render-key-value-internal-url>`
- [ ] `MAX_FILE_SIZE=10MB`
- [ ] `MAX_REQUEST_SIZE=25MB`
- [ ] `RATE_LIMIT_ENABLED=true`
- [ ] `RATE_LIMIT_MAX_REQUESTS=120`
- [ ] `RATE_LIMIT_WINDOW_SECONDS=60`
- [ ] `ERROR_METRICS_ENABLED=true`

## Portal Environment

- [ ] `ASPNETCORE_ENVIRONMENT=Production`
- [ ] `ASPNETCORE_URLS=http://+:10000`
- [ ] `ASPNETCORE_FORWARDEDHEADERS_ENABLED=true`
- [ ] `AaharRakshakApi__BaseUrl=https://<actual-api>.onrender.com`
- [ ] Verify cookie auth works over HTTPS.
- [ ] Verify redirects do not loop behind Render TLS.

## Android Public API Configuration

- [ ] Build with the final provider API URL:

```bash
cd android-app
gradle test lint assembleDebug -PAAHAR_API_BASE_URL=https://<actual-api>.onrender.com
```

- [ ] Confirm the APK path:

```text
android-app/app/build/outputs/apk/debug/app-debug.apk
```

- [ ] Install on emulator/device:

```bash
adb install -r android-app/app/build/outputs/apk/debug/app-debug.apk
adb shell monkey -p com.aaharrakshak.mobile 1
```

- [ ] Confirm citizen login, barcode lookup, complaint draft/submission and status tracking against the hosted API.
- [ ] Confirm HTTPS works and no cleartext production host is used.

## Public Smoke Tests

- [ ] `curl -fsS https://<actual-api>.onrender.com/api/v1/health`
- [ ] `curl -fsS https://<actual-api>.onrender.com/actuator/health`
- [ ] `curl -fsS https://<actual-api>.onrender.com/v3/api-docs`
- [ ] `curl -fsS https://<actual-portal>.onrender.com/health`
- [ ] Login API returns JWT for seeded demo users.
- [ ] Swagger authorizes with JWT.
- [ ] Portal public pages load.
- [ ] Portal official/company login works.
- [ ] WebSocket alerts authenticate with JWT.
- [ ] Redis alert publication does not fail in logs.
- [ ] Public report/search responses are anonymized.
- [ ] Company views only its notices and never citizen private data.

## Demo Workflow After Public Deploy

- [ ] Citizen login.
- [ ] Packaged-food barcode lookup for `8901234567890`.
- [ ] Packaged-food scan, correction, evidence metadata and complaint submission.
- [ ] Prepared-dish complaint with vendor/dish image metadata and GPS consent.
- [ ] District official assignment to inspector.
- [ ] Inspector scoped complaint access.
- [ ] Inspection schedule, geotag check-in, visit notes and evidence metadata.
- [ ] Sample collection and custody history.
- [ ] Lab assignment, sample receipt, report draft/submission.
- [ ] Senior official review/publish.
- [ ] Show-cause notice.
- [ ] Company response/document metadata.
- [ ] Senior official simulated decision.
- [ ] Public anonymized report.
- [ ] Recall list and safety alert.
- [ ] Hotspot dashboard.
- [ ] SLA escalation.

## Cost And Safety Controls

- [ ] Set provider spend limits or alerts wherever available.
- [ ] Document Render sleeping/cold-start behavior for demo presenters.
- [ ] Do not keep external uptime pingers running, because they can exhaust free instance-hours.
- [ ] Keep Render Key Value data non-critical because free persistence is unavailable.
- [ ] Keep R2 bucket private and quota-monitored.
- [ ] Use mock-only demo data.
- [ ] Disable or rotate demo seeded credentials before any non-demo audience.
- [ ] Do not connect real Aadhaar, real government APIs, real SMS/push providers, storefronts, delivery services or payment systems.

## Optional Custom Domain

- [ ] Do not purchase `aaharrakshak.in` without explicit approval.
- [ ] If later approved, buy/control the domain.
- [ ] Add `api.aaharrakshak.in` and `portal.aaharrakshak.in` custom domains in Render.
- [ ] Configure DNS records at the registrar/DNS provider.
- [ ] Wait for Render managed TLS certificates.
- [ ] Update API, portal and Android env/config values to the custom domain.

## Stop Conditions

Stop and ask before:

- Logging in to any provider.
- Creating external resources.
- Pushing source to a remote repository.
- Enabling billing/payment.
- Purchasing `aaharrakshak.in`.
- Uploading real evidence or citizen data.
- Adding real Aadhaar, government, SMS, payment, delivery or storefront integrations.
