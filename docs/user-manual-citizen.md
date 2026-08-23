# Citizen User Manual

## Login And Verification

1. Open the Android app.
2. Register with name, email, mobile number and password, or login with seeded credentials.
3. Use mock OTP `123456` when prompted.
4. The app stores JWT tokens in encrypted Android Keystore-backed storage.

## Packaged Food Complaint

1. Tap **Packaged scan**.
2. Scan the barcode/QR code. The app calls the public product lookup before manual entry.
3. Capture a package label image. The mock OCR adapter suggests product, company, FSSAI licence, batch and expiry details.
4. Correct any uncertain scanned values.
5. Add description, evidence metadata and GPS only if you consent.
6. Submit the complaint and note the tracking number.

Images and OCR are only complaint aids. They never confirm chemical adulteration without laboratory testing.

## Prepared Dish Or Street-Food Complaint

1. Tap **Prepared dish**.
2. Enter dish/vendor details. Company selection is not compulsory.
3. Add dish/vendor image metadata, optional video/receipt metadata and GPS with consent.
4. Save as offline draft or submit.

## Offline Drafts

Use **Save draft** when connectivity is weak. Drafts are stored on-device in Room and excluded from cloud backup. Submit once the API is reachable.

## Tracking And Reports

- View complaint history from **History**.
- Public complaint status and published lab reports hide citizen identity.
- PDF reports open through the device PDF viewer when a report URL/object key is available.

## Public Lookup And Alerts

- Search public product/company/licence/batch data from **Public lookup**.
- View recalls and regional safety alerts from **Alerts**.
- View aggregate hotspot maps from **Hotspots**. Citizen exact locations are not public.
- View vendor Trust Score from **Trust Score**. Raw complaints alone do not prove guilt.

## Privacy

- Full Aadhaar numbers, Aadhaar images and biometrics are never collected.
- GPS is optional and consent-based.
- Companies and public users cannot see citizen private details.
- Mock SMS/email/push providers are used for the academic demo.
