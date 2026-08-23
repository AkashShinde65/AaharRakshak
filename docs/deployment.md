# Deployment Guide

## Local Docker Compose

Copy the example environment file and replace placeholders before a real deployment:

```bash
cp infrastructure/.env.example infrastructure/.env
docker compose -f infrastructure/docker-compose.yml config
docker compose -f infrastructure/docker-compose.yml up -d mysql redis minio
```

Run the backend locally against Docker services:

```bash
cd backend-spring
DB_HOST=localhost DB_PORT=3306 DB_NAME=aaharrakshak DB_USERNAME=aahar DB_PASSWORD=aahar_password \
REDIS_HOST=localhost REDIS_PORT=6379 JWT_SECRET=replace-with-local-demo-secret mvn spring-boot:run
```

Run the ASP.NET portal:

```bash
dotnet run --project official-web-dotnet/official-web-dotnet.csproj --urls http://localhost:5080
```

Build the Android debug APK from a machine with Android SDK/Gradle:

```bash
cd android-app
gradle test lint assembleDebug -PAAHAR_API_BASE_URL=http://10.0.2.2:8080
```

## Docker Services

`infrastructure/docker-compose.yml` defines:

- `mysql`: MySQL 8.4 with Flyway-managed schema.
- `redis`: Redis 7 for alert Pub/Sub.
- `minio`: S3-compatible local object storage.
- `backend-api`: Spring Boot API using the Docker profile.
- `official-web`: ASP.NET Core MVC portal connected to `backend-api`.

The compose file is intended for academic/demo hosting and local verification. For real deployment, use managed MySQL/Redis/object storage where possible, set HTTPS at the ingress, and inject secrets through provider secret managers.

## Environment Variables

Required or important variables:

- `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`
- `MYSQL_ROOT_PASSWORD`
- `REDIS_HOST`, `REDIS_PORT`
- `MINIO_ROOT_USER`, `MINIO_ROOT_PASSWORD`
- `JWT_SECRET`
- `CORS_ALLOWED_ORIGINS`
- `PUBLIC_BASE_URL`
- `MAX_FILE_SIZE`, `MAX_REQUEST_SIZE`
- `RATE_LIMIT_ENABLED`, `RATE_LIMIT_MAX_REQUESTS`, `RATE_LIMIT_WINDOW_SECONDS`
- `ERROR_METRICS_ENABLED`
- `AaharRakshakApi__BaseUrl` for the ASP.NET portal
- `AAHAR_API_BASE_URL` Gradle property for Android builds

## Health, Logs And Error Monitoring

- API health: `GET /api/v1/health`
- Actuator health: `GET /actuator/health`
- Local metrics: authenticated `GET /actuator/metrics`
- Server-side error counter: `aaharrakshak.http.server.errors`
- Structured logs: JSON-style console logs from `backend-spring/src/main/resources/logback-spring.xml`

No external error-monitoring vendor is configured by default, which keeps the academic demo free and avoids sending sensitive complaint data to third parties.

## Free Hosting Options Evaluated On 2026-07-23

These options can be used for demos, not production safety systems.

| Provider | Fit For AaharRakshak | Free/Low-Cost Notes | Limitations |
| --- | --- | --- | --- |
| Render | Good for simple backend/portal demos using free web services. | Render documents free web services, static sites, Postgres and Key Value previews. | Free web services spin down after 15 minutes idle, local filesystem is ephemeral, free instance hours are capped, and free Postgres expires after 30 days. Source: <https://render.com/docs/free> |
| Railway | Good for short demo trial with Docker Compose-style services. | Railway documents a 30-day trial with a one-time $5 grant, then limited monthly free credit. | After trial, free credit is small; unverified/limited trials have network restrictions; stateful volumes are deleted after retention windows unless upgraded. Source: <https://docs.railway.com/pricing/free-trial> and <https://railway.com/pricing> |
| Koyeb | Good for one lightweight web service demo. | Koyeb documents a free instance with 512 MB RAM, 0.1 vCPU and 2 GB SSD for one web service per organization. | One free instance, no persistent volumes, limited regions, scales to zero after 1 hour idle. Source: <https://www.koyeb.com/docs/reference/instances> |
| Fly.io | Good low-cost container hosting, especially for custom Docker images. | Fly uses pay-as-you-go pricing; legacy free allowances only apply to older organizations that had legacy plans. | New users should not assume a general free plan. Source: <https://fly.io/docs/about/pricing/> |
| Google Cloud Run | Good for backend/portal containers if billing account is available. | Cloud Run has monthly free vCPU/RAM/request allowances. | MySQL, Redis and object storage are separate services; Artifact Registry/Cloud Build/networking can add costs. Source: <https://cloud.google.com/run/pricing> |
| Oracle Cloud Free Tier | Best free VM-style option if signup succeeds. | OCI Always Free includes compute and object storage resources after the $300/30-day trial. | Requires account signup, credit card/mobile verification, regional capacity can be constrained. Sources: <https://docs.oracle.com/iaas/Content/FreeTier/freetier.htm> and <https://docs.oracle.com/en-us/iaas/Content/FreeTier/freetier_topic-Always_Free_Resources.htm> |

## Recommended Free Demo Path

For a no-paid-resource academic demo:

1. Use Oracle Cloud Always Free or a local lab machine for the complete Docker Compose stack.
2. Use Render/Koyeb/Railway only for a short-lived backend or portal demo when their current free limits are acceptable.
3. Keep MinIO local unless the provider includes object storage without requiring payment.
4. Use mock email/SMS/push adapters only.

GitHub Student benefits are not required.

## Provider Subdomain Preparation

Example subdomain plan:

- API: `aaharrakshak-api.onrender.com`, `aaharrakshak-api.koyeb.app`, `aaharrakshak-api.up.railway.app`, `aaharrakshak-api.fly.dev` or a Cloud Run `run.app` URL.
- Portal: `aaharrakshak-portal.onrender.com`, `aaharrakshak-portal.koyeb.app`, `aaharrakshak-portal.up.railway.app`, `aaharrakshak-portal.fly.dev` or a Cloud Run `run.app` URL.
- Android Gradle property: `-PAAHAR_API_BASE_URL=https://aaharrakshak-api.<provider-domain>`.
- Spring CORS: `CORS_ALLOWED_ORIGINS=https://aaharrakshak-portal.<provider-domain>`.
- Portal API setting: `AaharRakshakApi__BaseUrl=https://aaharrakshak-api.<provider-domain>`.

Do not deploy publicly or create paid resources without permission.

## Optional Custom Domain: `aaharrakshak.in`

Only after purchasing or controlling the domain:

1. Add DNS records for `api.aaharrakshak.in` and `portal.aaharrakshak.in` to the chosen host.
2. Enable managed TLS certificates on the hosting provider.
3. Set `PUBLIC_BASE_URL=https://api.aaharrakshak.in`.
4. Set `CORS_ALLOWED_ORIGINS=https://portal.aaharrakshak.in`.
5. Build Android with `-PAAHAR_API_BASE_URL=https://api.aaharrakshak.in`.
6. Keep domain registration, DNS hosting and any paid plans outside source control and outside this project unless explicitly approved.
