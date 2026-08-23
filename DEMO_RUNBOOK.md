# AaharRakshak Final Demo Runbook

Date prepared: 2026-07-23.

Scope: final local demo rehearsal using mock demonstration data only. Do not connect real Aadhaar, real government licence registries, real SMS/push providers, real storefront/delivery/payment services or paid/public deployment resources.

## Demo URLs

| Service | URL |
| --- | --- |
| Spring Boot API | `http://localhost:8080` |
| API health | `http://localhost:8080/api/v1/health` |
| Swagger UI | `http://localhost:8080/swagger-ui.html` |
| ASP.NET Core MVC portal | `http://localhost:5080` |
| Portal public reports | `http://localhost:5080/Public/Reports` |
| MinIO console | `http://localhost:9001` |

## Demo Credentials

All seeded demo users use password `password`.

| Role | Email |
| --- | --- |
| Citizen | `citizen@aaharrakshak.dev` |
| Company | `company@aaharrakshak.dev` |
| Food Inspector | `inspector@aaharrakshak.dev` |
| Laboratory Officer | `lab@aaharrakshak.dev` |
| District Officer | `district@aaharrakshak.dev` |
| Central Administrator | `admin@aaharrakshak.dev` |

Mock OTP code: `123456`.

## Start Local Services

From the repository root:

```bash
docker compose -f infrastructure/docker-compose.yml up -d mysql redis minio
```

For a clean rehearsal database containing only Flyway mock demonstration data:

```bash
docker compose -f infrastructure/docker-compose.yml exec -T mysql mysql -uroot -proot_password -e "DROP DATABASE IF EXISTS aaharrakshak_demo_rehearsal; CREATE DATABASE aaharrakshak_demo_rehearsal; GRANT ALL PRIVILEGES ON aaharrakshak_demo_rehearsal.* TO 'aahar'@'%'; FLUSH PRIVILEGES;"
```

Start the API:

```bash
cd backend-spring
SPRING_PROFILES_ACTIVE=docker \
DB_HOST=localhost \
DB_PORT=3306 \
DB_NAME=aaharrakshak_demo_rehearsal \
DB_USERNAME=aahar \
DB_PASSWORD=aahar_password \
REDIS_HOST=localhost \
REDIS_PORT=6379 \
JWT_SECRET=<demo-only-secret-at-least-32-characters> \
java -jar target/backend-spring-0.0.1-SNAPSHOT.jar
```

Start the portal from a second terminal:

```bash
AaharRakshakApi__BaseUrl=http://localhost:8080 \
dotnet run --project official-web-dotnet/official-web-dotnet.csproj --urls http://localhost:5080
```

## Android APK

APK path:

```text
/Users/akashashokshinde/Desktop/Aaharrakshak/android-app/app/build/outputs/apk/debug/app-debug.apk
```

The rehearsal environment did not have `adb` installed, so the APK was not installed or opened on a device/emulator. To test it locally:

```bash
# Install platform tools first if adb is missing.
brew install android-platform-tools

# Start an Android emulator from Android Studio, then verify it is visible.
adb devices

# Install the debug APK.
adb install -r /Users/akashashokshinde/Desktop/Aaharrakshak/android-app/app/build/outputs/apk/debug/app-debug.apk

# Open the app.
adb shell monkey -p com.aaharrakshak.mobile 1
```

For an emulator, keep the Spring API running on the host at `http://localhost:8080`; the Android debug build defaults to `http://10.0.2.2:8080`. For a physical Android device, either rebuild with a LAN-accessible API URL or configure reverse/proxy networking appropriate to the device:

```bash
cd android-app
gradle assembleDebug -PAAHAR_API_BASE_URL=http://<host-lan-ip>:8080
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## Five-Minute Presentation Order

1. **System health and scope**: show `/api/v1/health`, portal home, Docker services, and remind the audience this is mock-only academic data.
2. **Citizen flow**: login as citizen, barcode lookup `8901234567890`, packaged-food scan, create complaint with image/video/receipt/GPS evidence, submit ticket.
3. **Dish complaint**: create prepared-dish complaint with unknown vendor, dish image, vendor image, receipt and GPS consent.
4. **Official workflow**: login as district officer, assign complaint to inspector, login as inspector, schedule inspection, geotag check-in, record visit evidence and collect sealed sample.
5. **Lab workflow**: login as lab officer, confirm sample receipt, create PDF report metadata with mock lab parameters, submit; district reviews and publishes.
6. **Due process**: issue show-cause notice, login as company, view notice and submit response document metadata, district approves simulated `BATCH_RECALL`.
7. **Public transparency**: show anonymized public complaint status, public report, public search, licence/batch status, recalls and safety alerts.
8. **Intelligence**: show Pune hotspot dashboard, run SLA escalation, show alert outbox and explain that citizen locations and raw complaints are not exposed as guilt.
9. **Android note**: show APK path and install steps; do not claim device testing unless an emulator/device walkthrough has been completed.

## Stop Services

Stop the API and portal with `Ctrl+C` in their terminals, then stop Docker services:

```bash
docker compose -f infrastructure/docker-compose.yml stop mysql redis minio
```

Verify ports are clear:

```bash
lsof -i :8080 -sTCP:LISTEN
lsof -i :5080 -sTCP:LISTEN
lsof -i :3306 -sTCP:LISTEN
lsof -i :6379 -sTCP:LISTEN
lsof -i :9000 -sTCP:LISTEN
lsof -i :9001 -sTCP:LISTEN
```
