# Known Limitations And Future Scope

## Known Limitations

- Android scanner, OCR, push and receipt OCR use mock/demo adapters until a legally approved integration is available.
- No real Aadhaar, biometric, government licence registry or SMS provider is integrated.
- Licence suspension/cancellation and external storefront/delivery/payment events are simulated only.
- MinIO is configured for local S3-compatible storage, but binary upload endpoints still rely on metadata/object-key flows in the backend.
- Free hosting tiers are unsuitable for production food-safety systems because of sleeping services, small memory, ephemeral storage or trial limits.
- Android APK generation depends on having Android SDK/Gradle installed locally or in CI.
- The risk analysis adapter is rule-based/mock; it is explainable but not a clinical/legal determination engine.
- Hotspot maps expose aggregate centers only; they are not a precise epidemiological model.

## Future Scope

- Add legally authorized FSSAI/licence registry integrations.
- Add production object storage with pre-signed upload/download URLs and malware scanning.
- Add real email/SMS/push providers after consent, DLT and privacy review.
- Add device barcode scanning and ML Kit OCR implementations behind the existing Android adapters.
- Add Android instrumented UI tests on physical/emulated devices.
- Add observability stack: OpenTelemetry traces, metrics dashboards and error monitoring.
- Add multilingual support beyond English/Hindi.
- Add role-specific Android modes for officials if required.
- Add formal DPIA/privacy-impact documentation before real-world deployment.
