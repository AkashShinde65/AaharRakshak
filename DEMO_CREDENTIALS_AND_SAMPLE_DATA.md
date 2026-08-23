# AaharRakshak Demo Credentials And Sample Data

Audit date: 2026-07-23.

## Seed Users

All seeded users use password `password`.

| Role | Email | Typical Use |
| --- | --- | --- |
| Citizen | `citizen@aaharrakshak.dev` | Register/verify mock OTP, scan product, file complaint, track reports |
| Company | `company@aaharrakshak.dev` | Manage company catalogue, view notices, submit response |
| Food Inspector | `inspector@aaharrakshak.dev` | View assigned complaints, schedule/check in, record visits, collect samples |
| Lab Officer | `lab@aaharrakshak.dev` | Receive assigned samples, draft and submit lab reports |
| District Officer | `district@aaharrakshak.dev` | Assign cases, publish reports, issue notices, approve simulated actions |
| Admin | `admin@aaharrakshak.dev` | Central administration and oversight |

Mock OTP code: `123456`.

## Product And Licence Seed Data

| Field | Value |
| --- | --- |
| Company | `Demo Foods Private Limited` |
| Trade name | `Demo Foods` |
| FSSAI licence | `12345678901234` |
| Product | `Demo Turmeric Powder` |
| Brand | `Demo Gold` |
| Category | `Spices` |
| Barcode/GTIN | `8901234567890` |
| Batch | `TUR-2026-001` |

## Workflow Seed Data

| Item | Value |
| --- | --- |
| Phase 4 complaint | `ARK-SEED-0001` |
| Phase 5 investigation complaint | `ARK-SEED-0005` |
| Phase 5 sample | `SMP-SEED-0005` / `SEAL-SEED-0005` |
| Phase 6 public complaint | `ARK-SEED-0006` |
| Phase 6 public lab report | `LAB-SEED-0006` |
| Phase 7 hotspot complaints | `ARK-HOT-0001` through `ARK-HOT-0010` |
| Phase 7 SLA escalation complaint | `ARK-SLA-0007` |

## Final Audit Live Data

| Item | Value |
| --- | --- |
| Packaged-food complaint | `ARK-20260723-934679` |
| Prepared-dish complaint | `ARK-20260723-299931` |
| Sample | `SMP-20260723-210511` |
| Lab report | `LAB-AUDIT-1784810048` |
| Show-cause notice | `SCN-20260723-955877` |
| Simulated administrative action | `ADM-20260723-137105` |

## Useful API Examples

Login:

```bash
curl -s http://localhost:8080/api/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"identifier":"citizen@aaharrakshak.dev","password":"password"}'
```

Public barcode lookup:

```bash
curl -s http://localhost:8080/api/v1/public/products/barcodes/8901234567890
```

Public complaint tracking:

```bash
curl -s http://localhost:8080/api/v1/public/transparency/complaints/ARK-SEED-0006/status
```

Public lab report:

```bash
curl -s http://localhost:8080/api/v1/public/transparency/reports/LAB-SEED-0006
```

Public recall list:

```bash
curl -s http://localhost:8080/api/v1/public/transparency/recalls
```

Authenticated alerts WebSocket:

```text
ws://localhost:8080/ws/alerts?access_token=<jwt>
```
