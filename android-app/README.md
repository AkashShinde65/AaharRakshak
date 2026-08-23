# AaharRakshak Android App

Native Kotlin Android app for Phase 8.

## Features

- Citizen registration, mock OTP and login.
- Android Keystore-backed encrypted JWT storage.
- Material Design Compose dashboard.
- Barcode-first packaged-food lookup.
- CameraX-ready package-image capture helper.
- Mock OCR extraction for product, company, FSSAI licence, batch and expiry.
- User confirmation/correction before complaint submission.
- Prepared-dish/street-food complaint flow with unknown vendor support.
- GPS location capture only with consent.
- Image, video and receipt evidence metadata.
- Room offline complaint drafts.
- Complaint history and tracking.
- Public product/company/licence/batch lookup.
- Recall and regional safety-alert screens.
- OpenStreetMap/Leaflet hotspot map in a WebView.
- Vendor Trust Score and receipt-backed review plumbing.
- WebSocket alert client and mock push fallback.
- English and Hindi resources.

Images and OCR results never confirm chemical adulteration without laboratory testing.

## Build

Install Android SDK and Gradle or open the project in Android Studio.

```bash
cd android-app
gradle test lint assembleDebug
```

Expected APK path:

```text
android-app/app/build/outputs/apk/debug/app-debug.apk
```

Override API URL:

```bash
gradle assembleDebug -PAAHAR_API_BASE_URL=https://aaharrakshak-api.example
```

Default emulator API URL is `http://10.0.2.2:8080`.
