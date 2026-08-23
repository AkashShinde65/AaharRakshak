# AaharRakshak MySQL Schema

The authoritative database schema lives in Flyway migrations:

- `backend-spring/src/main/resources/db/migration/V1__initial_schema.sql`
- `backend-spring/src/main/resources/db/migration/V2__authentication_and_security.sql`
- `backend-spring/src/main/resources/db/migration/V3__company_licence_product_batch_management.sql`
- `backend-spring/src/main/resources/db/migration/V4__citizen_complaint_scanning_system.sql`
- `backend-spring/src/main/resources/db/migration/V5__official_investigation_sample_lab_reporting.sql`
- `backend-spring/src/main/resources/db/migration/V6__transparency_administrative_action_web_portals.sql`
- `backend-spring/src/main/resources/db/migration/V7__advanced_intelligence_hotspots_alerts_sla_trust.sql`

Design principles:

- Use surrogate `BIGINT` primary keys for internal references.
- Keep licence, product, batch and complaint information normalized.
- Store media/report binaries outside MySQL and keep only metadata and object keys.
- Keep complainant private data on `users`; never expose it through public report projections.
- Store mock Aadhaar verification status/token only. Never store full Aadhaar numbers, images or biometric data.
- Track official and system actions in `audit_logs`.
- Model workflow status using constrained enums in application code and string columns in the database for portability.

## Phase 2 Auth Relationships

```mermaid
erDiagram
  users ||--o{ user_roles : has
  roles ||--o{ user_roles : assigned
  users ||--o{ refresh_tokens : owns
  users ||--o{ otp_verifications : verifies
  users ||--o{ companies : owns
  users ||--o{ audit_logs : performs
```

Phase 2 adds BCrypt password hashes, login attempt counters, lock timestamps, email/mobile verification flags, hashed refresh tokens and mock OTP rows. Company self-registration creates an account with the `COMPANY` role and a linked `companies` row with `PENDING_VERIFICATION` status.

## Phase 3 Company And Catalogue Relationships

```mermaid
erDiagram
  users ||--o{ companies : owns
  companies ||--o{ licences : submits
  users ||--o{ licences : reviews
  companies ||--o{ products : owns
  products ||--o{ product_barcodes : maps
  products ||--o{ batches : has
```

Phase 3 adds company profile fields, 14-digit FSSAI licence submissions, deterministic mock licence-registry review fields, product brand/category/manufacturer details, product and licence-label image metadata, normalized barcode/GTIN mappings and batch statuses: `ACTIVE`, `UNDER_INVESTIGATION`, `RECALLED`, `BLOCKED`.

The mock registry adapter does not scrape or call government systems. Product and licence images are represented by object metadata only; binaries stay outside MySQL.

## Phase 4 Complaint Scanning And Evidence Relationships

```mermaid
erDiagram
  users ||--o{ complaints : submits
  companies ||--o{ complaints : implicated
  products ||--o{ complaints : references
  batches ||--o{ complaints : references
  complaints ||--o{ evidence : includes
  complaints ||--o{ complaint_status_history : tracks
  users ||--o{ complaint_status_history : changes
  complaints ||--o{ assignments : receives
  users ||--o{ assignments : assigned_to
  users ||--o{ assignments : assigned_by
```

Phase 4 extends `complaints` with:

- `complaint_type`: `PACKAGED_FOOD` or `PREPARED_DISH`.
- Barcode and OCR fields for detected product/company/FSSAI/batch/expiry values.
- Corrected fields for citizen-confirmed product/company/FSSAI/batch/expiry values.
- Vendor name/address for unknown product or prepared-dish complaints.
- GPS consent, latitude, longitude, address and submitted timestamp fields.

Phase 4 extends `evidence` with original file name, size, checksum, captured timestamp and mock object-storage URI fields. Binaries remain outside MySQL. `complaint_status_history` stores the complaint timeline separately from the latest status on `complaints`, preserving normalized workflow history for drafts, submissions and later official transitions.

The V4 seed data adds one packaged-food complaint (`ARK-SEED-0001`), evidence metadata, status history and an assignment to the demo food inspector for authorization tests.

## Phase 5 Official Investigation Relationships

```mermaid
erDiagram
  complaints ||--o{ assignments : assigned
  users ||--o{ assignments : inspector
  complaints ||--o{ inspection_visits : schedules
  users ||--o{ inspection_visits : inspector
  inspection_visits ||--o{ inspection_evidence : has
  inspection_visits ||--o{ samples : collects
  complaints ||--o{ samples : yields
  users ||--o{ samples : collected_by
  samples ||--o{ sample_chain_of_custody : tracks
  users ||--o{ sample_chain_of_custody : actor
  samples ||--o{ sample_lab_assignments : assigned
  users ||--o{ sample_lab_assignments : lab_officer
  samples ||--o{ lab_reports : receives
  lab_reports ||--o{ lab_test_results : contains
  users ||--o{ lab_reports : submits
  users ||--o{ lab_reports : reviews
  users ||--o{ notifications : receives
```

Phase 5 adds:

- `complaints.district` and `complaints.sla_due_at` for district routing and investigation SLA tracking.
- `inspection_visits` for scheduling, geotagged check-in and visit notes.
- `inspection_evidence` for inspection image/video metadata with checksums and mock object-storage URIs.
- Additional `samples` columns for sample number, seal number, quantity, collection coordinates and storage details.
- `sample_chain_of_custody` for collection, transfer, lab receipt and report-state events.
- `sample_lab_assignments` for assigning samples to lab officers and confirming receipt.
- `lab_reports.status`, PDF metadata, checksum, submit/review/publish timestamps and reviewer links.
- `lab_test_results` for individual testing parameters, methods, limits, values and compliance flags.

The V5 seed data adds `ARK-SEED-0005`, a mock inspected complaint with a completed visit, inspection image metadata, sealed sample `SMP-SEED-0005` / `SEAL-SEED-0005`, custody entries and an assignment to `lab@aaharrakshak.dev`.

## Phase 6 Transparency And Administrative Action Relationships

```mermaid
erDiagram
  complaints ||--o{ show_cause_notices : triggers
  lab_reports ||--o{ show_cause_notices : supports
  companies ||--o{ show_cause_notices : receives
  users ||--o{ show_cause_notices : issues
  show_cause_notices ||--o{ company_notice_responses : receives
  users ||--o{ company_notice_responses : submits
  show_cause_notices ||--o{ actions : resolves
  lab_reports ||--o{ actions : supports
  companies ||--o{ actions : subject
  complaints ||--o{ actions : resolves
  complaints ||--o{ administrative_action_history : tracks
  show_cause_notices ||--o{ administrative_action_history : records
  actions ||--o{ administrative_action_history : records
  actions ||--o{ safety_alerts : publishes
  complaints ||--o{ safety_alerts : references
  companies ||--o{ safety_alerts : references
  products ||--o{ safety_alerts : references
  batches ||--o{ safety_alerts : references
```

Phase 6 adds:

- `lab_reports.outcome`: `SAFE`, `SUSPICIOUS`, `ADULTERATED` or `INCONCLUSIVE`.
- `show_cause_notices`: company notices linked to complaint, published lab report, company and issuing official.
- `company_notice_responses`: response text and document metadata/checksum for company submissions.
- Additional `actions` fields for report/company/notice links, simulated action number, reason, evidence, effective date and public summary.
- `administrative_action_history`: append-oriented workflow history for notice issuance, company response, review and decision.
- `safety_alerts`: public recall/safety alert records derived from simulated administrative decisions.

The V6 seed data adds `ARK-SEED-0006` with published report `LAB-SEED-0006` and `ADULTERATED` mock outcome for transparency and due-process workflow tests. This seed is separate from `ARK-SEED-0005` so Phase 5 sample/lab tests remain stable.

## Phase 7 Intelligence, Alerts, SLA And Trust Relationships

```mermaid
erDiagram
  complaints ||--o{ complaint_hotspot_members : clustered_as
  complaint_hotspots ||--o{ complaint_hotspot_members : contains
  complaints ||--o{ sla_escalations : escalated
  users ||--o{ sla_escalations : assigned_inspector
  users ||--o{ sla_escalations : escalated_to
  users ||--o{ alert_outbox : receives
  companies ||--o{ alert_outbox : references
  products ||--o{ alert_outbox : references
  batches ||--o{ alert_outbox : references
  companies ||--o{ vendor_reviews : receives
  users ||--o{ vendor_reviews : writes
  products ||--o{ vendor_reviews : references
  batches ||--o{ vendor_reviews : references
  companies ||--o{ vendor_trust_scores : scored
  complaints ||--o{ risk_analyses : analyzed
  actions ||--o{ mock_external_events : publishes
```

Phase 7 adds:

- `complaint_hotspots`: district-level aggregate clusters with related product/vendor key, risk level, complaint count, affected radius and center coordinates.
- `complaint_hotspot_members`: join table between hotspots and complaints.
- `alert_outbox`: durable retryable notification records for in-app, email, push and SMS mock channels.
- `sla_escalations`: high-risk overdue investigation escalation records with previous status, assigned inspector and senior official.
- `vendor_reviews`: receipt-backed reviews with receipt metadata/checksum and mock OCR verification token.
- `vendor_trust_scores`: calculated score components for inspections, lab outcomes, recalls and verified reviews.
- `risk_analyses`: explainable rule-based risk score snapshots and camera/OCR safety note.
- `mock_external_events`: simulated storefront, delivery and payment events for recalled/suspended batches or licences.

The V7 seed data adds `ARK-HOT-0001` through `ARK-HOT-0010` for hotspot detection and `ARK-SLA-0007` for overdue SLA escalation. V7 also moves the historical Phase 5 seed complaint SLA due date forward so the Phase 7 overdue demo does not mutate the Phase 5 lab-report workflow fixture.
