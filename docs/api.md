# API Guide

Base URL: `http://localhost:8080`

Use `Authorization: Bearer <accessToken>` for protected endpoints.

## Login

```bash
curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"identifier":"citizen@aaharrakshak.dev","password":"password"}'
```

## Packaged Food Complaint

```bash
curl -s http://localhost:8080/api/v1/public/products/barcodes/8901234567890
```

```bash
curl -s -X POST http://localhost:8080/api/v1/citizen/complaints/drafts \
  -H "Authorization: Bearer $CITIZEN_TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{
    "complaintType":"PACKAGED_FOOD",
    "category":"SUSPECTED_ADULTERATION",
    "scannedBarcode":"8901234567890",
    "confirmedProductName":"Demo Turmeric Powder",
    "confirmedCompanyName":"Demo Foods Private Limited",
    "confirmedFssaiLicenceNumber":"12345678901234",
    "confirmedBatchNumber":"TUR-2026-001",
    "confirmedExpiryDate":"2027-01-14",
    "description":"Citizen-confirmed label concern from Android.",
    "location":{"consentAccepted":true,"latitude":18.52043,"longitude":73.85674,"address":"Pune demo market"},
    "evidence":[{
      "type":"PRODUCT_LABEL_PHOTO",
      "objectKey":"complaints/mobile/product-label.jpg",
      "originalFileName":"product-label.jpg",
      "contentType":"image/jpeg",
      "sizeBytes":2048,
      "checksumSha256":"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
      "capturedAt":"2026-01-01T10:00:00Z"
    }]
  }'
```

Submit the returned draft id:

```bash
curl -s -X POST http://localhost:8080/api/v1/citizen/complaints/1/submit \
  -H "Authorization: Bearer $CITIZEN_TOKEN"
```

## Prepared Dish Complaint

Prepared-dish complaints can proceed with vendor text and GPS consent without requiring company selection.

```bash
curl -s -X POST http://localhost:8080/api/v1/citizen/complaints/drafts \
  -H "Authorization: Bearer $CITIZEN_TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{
    "complaintType":"PREPARED_DISH",
    "category":"HYGIENE_ISSUE",
    "vendorName":"Demo Street Vendor",
    "vendorAddress":"Pune demo market",
    "description":"Prepared-dish complaint with user-provided evidence.",
    "location":{"consentAccepted":true,"latitude":18.52043,"longitude":73.85674,"address":"Pune demo market"},
    "evidence":[
      {"type":"DISH_IMAGE","objectKey":"complaints/mobile/dish.jpg","originalFileName":"dish.jpg","contentType":"image/jpeg","sizeBytes":2048,"checksumSha256":"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb","capturedAt":"2026-01-01T10:00:00Z"},
      {"type":"VENDOR_IMAGE","objectKey":"complaints/mobile/vendor.jpg","originalFileName":"vendor.jpg","contentType":"image/jpeg","sizeBytes":2048,"checksumSha256":"cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc","capturedAt":"2026-01-01T10:01:00Z"}
    ]
  }'
```

## Public Transparency

```bash
curl -s http://localhost:8080/api/v1/public/transparency/complaints/ARK-SEED-0006/status
curl -s http://localhost:8080/api/v1/public/transparency/reports/LAB-SEED-0006
curl -s 'http://localhost:8080/api/v1/public/transparency/search?company=Demo'
curl -s http://localhost:8080/api/v1/public/transparency/licences/12345678901234/status
curl -s http://localhost:8080/api/v1/public/transparency/batches/TUR-2026-001/status
curl -s http://localhost:8080/api/v1/public/transparency/recalls
curl -s http://localhost:8080/api/v1/public/transparency/alerts
```

## Intelligence And Alerts

```bash
curl -s 'http://localhost:8080/api/v1/official/intelligence/hotspots/district?district=Pune' \
  -H "Authorization: Bearer $DISTRICT_TOKEN"

curl -s -X POST http://localhost:8080/api/v1/official/intelligence/sla/check-overdue \
  -H "Authorization: Bearer $DISTRICT_TOKEN"

curl -s http://localhost:8080/api/v1/citizen/alerts \
  -H "Authorization: Bearer $CITIZEN_TOKEN"

curl -s http://localhost:8080/api/v1/public/trust/companies/1
```

Real-time alerts:

```text
ws://localhost:8080/ws/alerts?access_token=<jwt>
```

## Due Process Workflow

```bash
curl -s -X POST http://localhost:8080/api/v1/official/admin-actions/reports/1/show-cause-notices \
  -H "Authorization: Bearer $DISTRICT_TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"reason":"Published non-safe mock lab outcome requires company response.","responseDueAt":"2026-02-01T10:00:00Z"}'
```

```bash
curl -s -X POST http://localhost:8080/api/v1/company/admin-actions/notices/SCN-000001/responses \
  -H "Authorization: Bearer $COMPANY_TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{
    "responseText":"Company submits a mock corrective action response.",
    "document":{"objectKey":"company/responses/scn-response.pdf","originalFileName":"scn-response.pdf","contentType":"application/pdf","sizeBytes":2048,"checksumSha256":"dddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd"}
  }'
```

```bash
curl -s -X POST http://localhost:8080/api/v1/official/admin-actions/notices/SCN-000001/decision \
  -H "Authorization: Bearer $DISTRICT_TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"actionType":"WARNING","reason":"Academic demo simulated decision.","effectiveFrom":"2026-02-02T10:00:00Z"}'
```
