# AaharRakshak Agent Rules

These rules are permanent project guidance for any coding agent working in this repository.

## Project Boundaries

- Treat `AaharRakshak_PROJECT_BRIEF.md` as the source of truth.
- Work in small phases and keep every completed phase runnable and tested.
- Do not start a later phase unless the user explicitly asks for it.
- Preserve the recommended repository layout:
  - `docs/`
  - `backend-spring/`
  - `official-web-dotnet/`
  - `android-app/`
  - `database/`
  - `infrastructure/`
  - `postman/`

## Architecture Rules

- Use a modular monolith first.
- Keep business rules in the Spring Boot API, not duplicated in web or Android clients.
- Follow layered architecture, DTOs, validation, exception handling, clean code and SOLID principles.
- Use Java 21, Spring Boot 3, Maven, Spring Data JPA, Bean Validation, Flyway, MySQL 8 and OpenAPI for the backend.
- Add repositories, services, controllers and DTOs in module packages that match the domain.
- Prefer interfaces for external integrations so mock adapters can be used in the academic demo.

## Privacy and Legal Safety

- Never store full Aadhaar numbers, Aadhaar images, biometric data or unauthorized government registry data.
- Aadhaar or identity verification must be mock/consent-based unless a legally authorized integration is explicitly provided.
- Companies and public reports must never expose complainant private details.
- Public reports must be anonymized.
- Do not implement automatic real licence suspension, paid SMS, proprietary government APIs or unverified AI claims.
- Camera/AI features may suggest food type or visible warning signs only; they must not claim chemical adulteration without inspection and lab testing.

## Database Rules

- Use Flyway migrations for all schema changes.
- Keep schema normalized: identity/RBAC, company/licence, catalogue, complaints, evidence, investigation, notifications and audit logs stay separate.
- Store media and lab-report binaries outside MySQL; keep only metadata/object keys in the database.
- Important official/system actions must create immutable-style audit log records.
- Verify ER diagrams and docs when relationships change.

## Testing and Verification

- Before marking a phase complete, run:
  - `mvn clean verify` from `backend-spring/`
  - `docker compose -f infrastructure/docker-compose.yml config` from the repo root
- When database changes are made, start MySQL with Docker Compose and verify Flyway migrations against MySQL, not only H2.
- When API changes are made, start the Spring Boot API and test `GET /api/v1/health`.
- Fix build, migration, relationship and health-check errors before reporting completion.

