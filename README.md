# AaharRakshak

AaharRakshak is an academic national food-safety platform for citizen complaints, official investigations, company/FBO due process, public transparency, hotspots, alerts, Trust Score and a native Android citizen app.

Tagline: **Scan Karo. Report Karo. Surakshit Raho.**

## Current Scope

The repository now contains Phase 1 through Phase 8 delivery:

- Spring Boot modular-monolith backend with Java 21, Maven, MySQL, Flyway V1-V7, Spring Security, JWT, RBAC, OpenAPI, secure headers, rate limiting, health checks, structured logs, local error metrics and tests.
- ASP.NET Core MVC portal with Official, Company and Public Areas consuming Spring APIs through `HttpClient`.
- Native Kotlin Android app scaffold using Material Design Compose, Retrofit, Room, Android Keystore-backed JWT storage, CameraX-ready capture, mock OCR/barcode/push adapters, offline drafts, Hindi/English resources and OpenStreetMap hotspot rendering.
- Docker Compose for Spring Boot, MySQL, Redis, MinIO and the ASP.NET portal.
- Documentation for architecture, deployment, user manuals, tests, demo script, viva/interview explanation, limitations and future scope.

The system intentionally avoids real Aadhaar integrations, unauthorized government data, real licence cancellation, real SMS and paid services. Licence suspension/cancellation and external marketplace/payment actions are simulated platform records only.

## Repository Layout

```text
AaharRakshak/
  android-app/             Native Android Kotlin app
  backend-spring/          Spring Boot REST API and Flyway migrations
  docs/                    Architecture, deployment, manuals and reports
  infrastructure/          Docker Compose and environment templates
  official-web-dotnet/     ASP.NET Core MVC portal
  postman/                 Phase API examples
  PROJECT_STATUS.md
  README.md
```

## Demo Credentials

All seeded users use password `password`.

| Role | Email |
| --- | --- |
| Citizen | `citizen@aaharrakshak.dev` |
| Company | `company@aaharrakshak.dev` |
| Food Inspector | `inspector@aaharrakshak.dev` |
| Laboratory Officer | `lab@aaharrakshak.dev` |
| District Officer | `district@aaharrakshak.dev` |
| Central Administrator | `admin@aaharrakshak.dev` |

Mock OTP code: `123456`.

## Run Backend Tests

```bash
cd backend-spring
mvn clean verify
```

## Run Local Services

```bash
docker compose -f infrastructure/docker-compose.yml up -d mysql redis minio
```

Useful local service URLs:

- Spring API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- API health: `http://localhost:8080/api/v1/health`
- ASP.NET portal: `http://localhost:5080` or local `dotnet run` URL
- MinIO API: `http://localhost:9000`
- MinIO console: `http://localhost:9001`

## Run Backend

```bash
cd backend-spring
mvn spring-boot:run
```

## Run Portal

```bash
dotnet build official-web-dotnet/official-web-dotnet.csproj
dotnet test official-web-dotnet.Tests/official-web-dotnet.Tests.csproj
dotnet run --project official-web-dotnet/official-web-dotnet.csproj --urls http://localhost:5080
```

## Build Android

Android tooling is required locally: Android SDK, Gradle or Android Studio, and a compatible JDK for the Android Gradle Plugin.

```bash
cd android-app
gradle test lint assembleDebug
```

Expected debug APK path after a successful Android build:

```text
android-app/app/build/outputs/apk/debug/app-debug.apk
```

The default Android API URL is `http://10.0.2.2:8080` for the emulator. Override it with:

```bash
gradle assembleDebug -PAAHAR_API_BASE_URL=https://your-api-subdomain.example
```

## Important API Areas

- Auth: `/api/v1/auth/**`
- Citizen complaints: `/api/v1/citizen/complaints/**`
- Product lookup: `/api/v1/public/products/**`
- Official investigations: `/api/v1/official/investigations/**`
- Lab reports: `/api/v1/lab/investigations/**`
- Administrative action: `/api/v1/official/admin-actions/**`
- Company notices: `/api/v1/company/admin-actions/**`
- Public transparency: `/api/v1/public/transparency/**`
- Intelligence: `/api/v1/official/intelligence/**`
- Public Trust Score: `/api/v1/public/trust/companies/{companyId}`
- Authenticated alerts WebSocket: `/ws/alerts?access_token={jwt}`

## Documentation

- [Architecture](docs/architecture.md)
- [Security](docs/security.md)
- [API Guide](docs/api.md)
- [Deployment Guide](docs/deployment.md)
- [Citizen Manual](docs/user-manual-citizen.md)
- [Company Manual](docs/user-manual-company.md)
- [Official Manual](docs/user-manual-official.md)
- [Test Report](docs/test-report.md)
- [Demo Script](docs/demo-script.md)
- [Viva Explanation](docs/viva-explanation.md)
- [Known Limitations And Future Scope](docs/known-limitations-future-scope.md)
