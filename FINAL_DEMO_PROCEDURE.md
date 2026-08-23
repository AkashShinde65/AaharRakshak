# AaharRakshak Five-Minute Demonstration Procedure

Audit date: 2026-07-23.

## 0:00-0:45 - Start The Stack

```bash
docker compose -f infrastructure/docker-compose.yml up -d mysql redis minio
cd backend-spring
mvn clean verify
DB_HOST=localhost DB_PORT=3306 DB_NAME=aaharrakshak DB_USERNAME=aahar DB_PASSWORD=aahar_password \
REDIS_HOST=localhost REDIS_PORT=6379 \
JWT_SECRET=replace-with-local-demo-secret-please-use-generated-secret-in-production \
mvn spring-boot:run
```

Open:

- API health: `http://localhost:8080/api/v1/health`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

## 0:45-1:30 - Citizen Scan And Complaint

1. Login as `citizen@aaharrakshak.dev` / `password`.
2. Call barcode lookup for `8901234567890`.
3. Show the packaged-food scan response: product matched, OCR hints present, and safety note says image/OCR cannot prove adulteration.
4. Create a draft complaint, add image/receipt evidence metadata with checksum, then submit to receive an `ARK-...` tracking number.

## 1:30-2:15 - Official Investigation

1. Login as `district@aaharrakshak.dev` and assign the complaint to `inspector@aaharrakshak.dev`.
2. Login as inspector and show only assigned complaints.
3. Schedule inspection, geotag check-in, record visit notes/evidence, and collect a sealed sample.
4. Show custody history and the SLA due date.

## 2:15-3:00 - Laboratory Report

1. District officer assigns the sample to `lab@aaharrakshak.dev`.
2. Lab officer confirms receipt, creates a PDF report metadata draft, adds parameters and submits.
3. District officer reviews and publishes the report.
4. Show that citizen/public status updates reveal progress without exposing sensitive official details.

## 3:00-3:45 - Due Process And Transparency

1. Use `LAB-SEED-0006` or the live published report to issue a show-cause notice.
2. Login as company and submit a response document metadata record.
3. Login as district officer and approve a simulated action such as `BATCH_RECALL`.
4. Open public report/search/recall endpoints and point out citizen redaction and simulated action wording.

## 3:45-4:30 - Intelligence And Alerts

1. Run hotspot detection for Pune and show the `CRITICAL` aggregate cluster from seeded complaints.
2. Run SLA overdue check and show escalation to a senior official.
3. Open public Trust Score for company `1` and explain that raw complaints do not directly prove guilt.
4. Mention Redis Pub/Sub, authenticated WebSocket alerts and durable outbox retry records.

## 4:30-5:00 - Portal, Android And Boundaries

1. Start the portal:

```bash
dotnet run --project official-web-dotnet/official-web-dotnet.csproj --urls http://localhost:5080
```

2. Show Official, Company and Public areas at `http://localhost:5080`.
3. Show Android APK path:

```text
android-app/app/build/outputs/apk/debug/app-debug.apk
```

4. Close with boundaries: no real Aadhaar, no unauthorized government data, no real SMS, no real licence cancellation and no image-only adulteration claims.

## Optional Complete Docker Stack

```bash
docker compose -f infrastructure/docker-compose.yml up --build
```

Services:

- Spring API: `http://localhost:8080`
- ASP.NET portal: `http://localhost:5080`
- MinIO console: `http://localhost:9001`
