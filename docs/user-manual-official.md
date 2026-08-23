# Official User Manual

## Login

Seeded users:

- Food Inspector: `inspector@aaharrakshak.dev`
- Lab Officer: `lab@aaharrakshak.dev`
- District Officer: `district@aaharrakshak.dev`
- Admin: `admin@aaharrakshak.dev`
- Password: `password`

## Complaint And Investigation

1. District officers/admins view the priority dashboard.
2. Assign complaints by district/location to inspectors.
3. Inspectors view only assigned complaints.
4. Inspectors schedule visits, check in with geotagged location, add notes/evidence metadata and collect sealed samples.
5. Chain-of-custody history is append-oriented.
6. Senior officials assign samples to lab officers.
7. Lab officers confirm receipt, create draft reports, add test parameters and submit.
8. Senior officials review/publish reports.

## Administrative Action

1. Issue show-cause notices only after relevant published non-safe lab outcomes.
2. Review company responses and documents.
3. Record final simulated actions: `WARNING`, `BATCH_RECALL`, `TEMPORARY_SUSPENSION` or `CANCELLATION`.
4. Record reason, evidence, effective date and approving official.

Inspectors and lab officers cannot approve their own cases. Simulated cancellation/suspension never triggers a real government registry action.

## Hotspots, Alerts And SLA

- Run district hotspot detection from the Official dashboard.
- Hotspots use configurable radius/time windows; default critical rule is 10 related complaints within 24 hours.
- Trigger SLA checks for overdue high-risk investigations. Default high-risk SLA is 48 hours.
- Review alert outbox and retry due alerts.
- Inspect mock external events for recalls/suspensions without disabling real accounts.

## Privacy

Do not disclose citizen identity, phone, email, exact private GPS points, internal notes or chain-of-custody details outside authorized official workflows.
