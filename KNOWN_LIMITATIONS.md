# AaharRakshak Known Limitations

Audit date: 2026-07-23.

## Integration Boundaries

- Aadhaar verification is mock/consent-based only. The system stores verification status/token and does not collect full Aadhaar numbers, Aadhaar images or biometrics.
- FSSAI/government licence checks use a deterministic mock adapter. The system does not scrape or call unauthorized government systems.
- Licence suspension, cancellation, batch recall and external lockdown events are simulated platform records only. No real government, storefront, delivery or payment account is changed.
- Email, SMS, push and OCR providers are mock/local demonstration adapters.
- MinIO is included in Docker Compose and health-checked; the application stores file metadata and mock object keys rather than real uploaded binary streams in this academic implementation.

## Verification Limits

- Android unit tests, lint and debug APK build passed, but no physical Android device or emulator walkthrough was performed during this audit.
- CameraX capture, device GPS, OS-level notification delivery, barcode scanner UX and PDF viewer behavior are not hardware verified.
- ASP.NET portal build and tests passed, but no automated visual regression or cross-browser responsive test suite exists.
- Free-hosting provider options are documented, but no public deployment or provider subdomain was created.

## Security And Production Readiness

- Development seed users all use password `password`; these accounts are for local demonstration only.
- Docker Compose and `application.yml` include clearly named development fallback values. Production profile uses environment variables and must be supplied with generated secrets.
- The Android debug APK is unsigned for production distribution. A release signing process and secure keystore management are still required before real app distribution.
- Observability is local/demo oriented: health checks, structured logs and local metrics are present, but external monitoring and incident response are not configured.

## Data And AI Limits

- Barcode/OCR/AI outputs are triage aids only. The UI and API safety notes state that images cannot prove chemical adulteration without inspection and laboratory confirmation.
- Trust Score calculation avoids treating raw complaints as proof of guilt. It uses verified inspections, lab outcomes, simulated recalls and receipt-backed reviews.
- Hotspot coordinates are aggregate district-level centers; they are not precise citizen locations.

## Operational Limits

- This release has been validated on a local Docker MySQL/Redis/MinIO stack.
- Load, performance, accessibility audit tooling, penetration testing and disaster-recovery testing were not performed.
- Payment, domain purchase, real SMS and public deployment were intentionally not performed without explicit permission.
