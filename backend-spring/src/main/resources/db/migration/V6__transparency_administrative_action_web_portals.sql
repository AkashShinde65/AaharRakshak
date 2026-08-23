ALTER TABLE lab_reports ADD COLUMN outcome VARCHAR(40) NOT NULL DEFAULT 'INCONCLUSIVE';

CREATE TABLE show_cause_notices (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    notice_number VARCHAR(80) NOT NULL UNIQUE,
    complaint_id BIGINT NOT NULL,
    lab_report_id BIGINT NOT NULL,
    company_id BIGINT NOT NULL,
    issued_by_user_id BIGINT NOT NULL,
    subject VARCHAR(180) NOT NULL,
    reason VARCHAR(1000) NOT NULL,
    evidence_summary VARCHAR(1200) NOT NULL,
    response_due_at TIMESTAMP NOT NULL,
    status VARCHAR(40) NOT NULL,
    issued_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_show_cause_notices_complaint FOREIGN KEY (complaint_id) REFERENCES complaints(id),
    CONSTRAINT fk_show_cause_notices_report FOREIGN KEY (lab_report_id) REFERENCES lab_reports(id),
    CONSTRAINT fk_show_cause_notices_company FOREIGN KEY (company_id) REFERENCES companies(id),
    CONSTRAINT fk_show_cause_notices_issued_by FOREIGN KEY (issued_by_user_id) REFERENCES users(id)
);

ALTER TABLE actions ADD COLUMN lab_report_id BIGINT;
ALTER TABLE actions ADD COLUMN company_id BIGINT;
ALTER TABLE actions ADD COLUMN notice_id BIGINT;
ALTER TABLE actions ADD COLUMN action_number VARCHAR(80);
ALTER TABLE actions ADD COLUMN reason VARCHAR(1000);
ALTER TABLE actions ADD COLUMN evidence_summary VARCHAR(1200);
ALTER TABLE actions ADD COLUMN effective_date DATE;
ALTER TABLE actions ADD COLUMN simulated BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE actions ADD COLUMN public_summary VARCHAR(1000);
ALTER TABLE actions ADD CONSTRAINT fk_actions_lab_report FOREIGN KEY (lab_report_id) REFERENCES lab_reports(id);
ALTER TABLE actions ADD CONSTRAINT fk_actions_company FOREIGN KEY (company_id) REFERENCES companies(id);
ALTER TABLE actions ADD CONSTRAINT fk_actions_notice FOREIGN KEY (notice_id) REFERENCES show_cause_notices(id);

CREATE TABLE company_notice_responses (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    notice_id BIGINT NOT NULL,
    submitted_by_user_id BIGINT NOT NULL,
    response_text VARCHAR(3000) NOT NULL,
    object_key VARCHAR(500) NOT NULL,
    original_file_name VARCHAR(180),
    content_type VARCHAR(120),
    file_size_bytes BIGINT,
    checksum_sha256 VARCHAR(64) NOT NULL,
    storage_uri VARCHAR(600),
    submitted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_company_notice_responses_notice FOREIGN KEY (notice_id) REFERENCES show_cause_notices(id),
    CONSTRAINT fk_company_notice_responses_submitted_by FOREIGN KEY (submitted_by_user_id) REFERENCES users(id)
);

CREATE TABLE administrative_action_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    complaint_id BIGINT NOT NULL,
    notice_id BIGINT,
    action_id BIGINT,
    actor_user_id BIGINT,
    event_type VARCHAR(80) NOT NULL,
    notes VARCHAR(1000),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_admin_action_history_complaint FOREIGN KEY (complaint_id) REFERENCES complaints(id),
    CONSTRAINT fk_admin_action_history_notice FOREIGN KEY (notice_id) REFERENCES show_cause_notices(id),
    CONSTRAINT fk_admin_action_history_action FOREIGN KEY (action_id) REFERENCES actions(id),
    CONSTRAINT fk_admin_action_history_actor FOREIGN KEY (actor_user_id) REFERENCES users(id)
);

CREATE TABLE safety_alerts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    action_id BIGINT,
    complaint_id BIGINT NOT NULL,
    company_id BIGINT,
    product_id BIGINT,
    batch_id BIGINT,
    title VARCHAR(180) NOT NULL,
    message VARCHAR(1200) NOT NULL,
    location_text VARCHAR(120),
    severity VARCHAR(40) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    published_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_safety_alerts_action FOREIGN KEY (action_id) REFERENCES actions(id),
    CONSTRAINT fk_safety_alerts_complaint FOREIGN KEY (complaint_id) REFERENCES complaints(id),
    CONSTRAINT fk_safety_alerts_company FOREIGN KEY (company_id) REFERENCES companies(id),
    CONSTRAINT fk_safety_alerts_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_safety_alerts_batch FOREIGN KEY (batch_id) REFERENCES batches(id)
);

CREATE INDEX idx_show_cause_notices_company ON show_cause_notices (company_id, issued_at);
CREATE INDEX idx_show_cause_notices_report ON show_cause_notices (lab_report_id, issued_at);
CREATE INDEX idx_company_notice_responses_notice ON company_notice_responses (notice_id, submitted_at);
CREATE INDEX idx_admin_action_history_complaint ON administrative_action_history (complaint_id, created_at);
CREATE INDEX idx_actions_report ON actions (lab_report_id, decided_at);
CREATE INDEX idx_actions_number ON actions (action_number);
CREATE INDEX idx_safety_alerts_active ON safety_alerts (active, published_at);

INSERT INTO complaints (
    ticket_number,
    citizen_id,
    company_id,
    product_id,
    batch_id,
    complaint_type,
    category,
    status,
    description,
    scanned_barcode,
    detected_product_name,
    detected_company_name,
    detected_fssai_licence_number,
    detected_batch_number,
    detected_expiry_date,
    confirmed_product_name,
    confirmed_company_name,
    confirmed_fssai_licence_number,
    confirmed_batch_number,
    confirmed_expiry_date,
    latitude,
    longitude,
    location_text,
    gps_consent,
    risk_score,
    district,
    sla_due_at,
    submitted_at
)
SELECT
    'ARK-SEED-0006',
    citizen.id,
    company.id,
    product.id,
    batch.id,
    'PACKAGED_FOOD',
    'SUSPECTED_ADULTERATION',
    'REPORT_PUBLISHED',
    'Seeded Phase 6 complaint with anonymized public laboratory report ready for due-process workflow.',
    '8901234567890',
    'Demo Turmeric Powder',
    'Demo Foods Private Limited',
    '12345678901234',
    'TUR-2026-001',
    batch.expires_on,
    'Demo Turmeric Powder',
    'Demo Foods Private Limited',
    '12345678901234',
    'TUR-2026-001',
    batch.expires_on,
    18.5204300,
    73.8567400,
    'Pune demo market',
    TRUE,
    88,
    'Pune',
    '2026-01-04 09:00:00',
    '2026-01-01 09:00:00'
FROM users citizen
JOIN companies company ON company.legal_name = 'Demo Foods Private Limited'
JOIN products product ON product.company_id = company.id AND product.barcode = '8901234567890'
JOIN batches batch ON batch.product_id = product.id AND batch.batch_number = 'TUR-2026-001'
WHERE citizen.email = 'citizen@aaharrakshak.dev';

INSERT INTO complaint_status_history (complaint_id, status, changed_by_user_id, notes)
SELECT c.id, 'DRAFT', citizen.id, 'Seeded Phase 6 draft'
FROM complaints c
JOIN users citizen ON citizen.email = 'citizen@aaharrakshak.dev'
WHERE c.ticket_number = 'ARK-SEED-0006';

INSERT INTO complaint_status_history (complaint_id, status, changed_by_user_id, notes)
SELECT c.id, 'SUBMITTED', citizen.id, 'Seeded Phase 6 submitted complaint'
FROM complaints c
JOIN users citizen ON citizen.email = 'citizen@aaharrakshak.dev'
WHERE c.ticket_number = 'ARK-SEED-0006';

INSERT INTO complaint_status_history (complaint_id, status, changed_by_user_id, notes)
SELECT c.id, 'VERIFIED', admin.id, 'Seeded Phase 6 verified complaint'
FROM complaints c
JOIN users admin ON admin.email = 'admin@aaharrakshak.dev'
WHERE c.ticket_number = 'ARK-SEED-0006';

INSERT INTO complaint_status_history (complaint_id, status, changed_by_user_id, notes)
SELECT c.id, 'ASSIGNED', admin.id, 'Seeded Phase 6 assigned complaint'
FROM complaints c
JOIN users admin ON admin.email = 'admin@aaharrakshak.dev'
WHERE c.ticket_number = 'ARK-SEED-0006';

INSERT INTO complaint_status_history (complaint_id, status, changed_by_user_id, notes)
SELECT c.id, 'INSPECTION_SCHEDULED', inspector.id, 'Seeded Phase 6 inspection scheduled'
FROM complaints c
JOIN users inspector ON inspector.email = 'inspector@aaharrakshak.dev'
WHERE c.ticket_number = 'ARK-SEED-0006';

INSERT INTO complaint_status_history (complaint_id, status, changed_by_user_id, notes)
SELECT c.id, 'SAMPLE_COLLECTED', inspector.id, 'Seeded Phase 6 sample collected'
FROM complaints c
JOIN users inspector ON inspector.email = 'inspector@aaharrakshak.dev'
WHERE c.ticket_number = 'ARK-SEED-0006';

INSERT INTO complaint_status_history (complaint_id, status, changed_by_user_id, notes)
SELECT c.id, 'LAB_TESTING', lab.id, 'Seeded Phase 6 lab testing completed'
FROM complaints c
JOIN users lab ON lab.email = 'lab@aaharrakshak.dev'
WHERE c.ticket_number = 'ARK-SEED-0006';

INSERT INTO complaint_status_history (complaint_id, status, changed_by_user_id, notes)
SELECT c.id, 'REPORT_PUBLISHED', admin.id, 'Seeded Phase 6 public report published'
FROM complaints c
JOIN users admin ON admin.email = 'admin@aaharrakshak.dev'
WHERE c.ticket_number = 'ARK-SEED-0006';

INSERT INTO assignments (complaint_id, assigned_to_user_id, assigned_by_user_id, assigned_at, notes)
SELECT c.id, inspector.id, admin.id, '2026-01-01 09:20:00', 'Seeded Phase 6 assignment'
FROM complaints c
JOIN users inspector ON inspector.email = 'inspector@aaharrakshak.dev'
JOIN users admin ON admin.email = 'admin@aaharrakshak.dev'
WHERE c.ticket_number = 'ARK-SEED-0006';

INSERT INTO inspection_visits (
    complaint_id,
    inspector_user_id,
    scheduled_by_user_id,
    scheduled_at,
    status,
    check_in_at,
    check_in_latitude,
    check_in_longitude,
    location_text,
    visit_notes,
    completed_at
)
SELECT c.id, inspector.id, admin.id, '2026-01-02 09:30:00', 'COMPLETED',
       '2026-01-02 09:35:00', 18.5204300, 73.8567400, 'Pune demo market',
       'Seeded Phase 6 inspection visit. Image evidence alone does not prove adulteration.',
       '2026-01-02 10:10:00'
FROM complaints c
JOIN users inspector ON inspector.email = 'inspector@aaharrakshak.dev'
JOIN users admin ON admin.email = 'admin@aaharrakshak.dev'
WHERE c.ticket_number = 'ARK-SEED-0006';

INSERT INTO samples (
    complaint_id,
    inspection_visit_id,
    collected_by_user_id,
    sample_number,
    seal_number,
    quantity,
    chain_of_custody,
    collected_at,
    latitude,
    longitude,
    location_text,
    storage_details
)
SELECT c.id, v.id, inspector.id, 'SMP-SEED-0006', 'SEAL-SEED-0006', '250 g',
       'Seeded Phase 6 sample for administrative action workflow.',
       '2026-01-02 10:15:00', 18.5204300, 73.8567400,
       'Pune demo market', 'Sterile container, cold-box slot B1'
FROM complaints c
JOIN inspection_visits v ON v.complaint_id = c.id
JOIN users inspector ON inspector.email = 'inspector@aaharrakshak.dev'
WHERE c.ticket_number = 'ARK-SEED-0006';

INSERT INTO sample_lab_assignments (
    sample_id,
    assigned_to_user_id,
    assigned_by_user_id,
    status,
    assigned_at,
    received_at,
    received_by_user_id,
    notes
)
SELECT s.id, lab.id, admin.id, 'RECEIVED', '2026-01-02 10:30:00',
       '2026-01-02 11:00:00', lab.id, 'Seeded Phase 6 laboratory receipt'
FROM samples s
JOIN complaints c ON c.id = s.complaint_id
JOIN users lab ON lab.email = 'lab@aaharrakshak.dev'
JOIN users admin ON admin.email = 'admin@aaharrakshak.dev'
WHERE c.ticket_number = 'ARK-SEED-0006';

INSERT INTO lab_reports (
    sample_id,
    report_number,
    object_key,
    result_summary,
    uploaded_at,
    status,
    outcome,
    original_file_name,
    content_type,
    file_size_bytes,
    checksum_sha256,
    storage_uri,
    submitted_by_user_id,
    submitted_at,
    reviewed_by_user_id,
    reviewed_at,
    published_at
)
SELECT s.id, 'LAB-SEED-0006', 'lab-reports/LAB-SEED-0006.pdf',
       'Mock lab outcome: adulterated demo sample', '2026-01-02 12:00:00',
       'PUBLISHED', 'ADULTERATED', 'LAB-SEED-0006.pdf', 'application/pdf',
       262144, 'eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee',
       'local-mock-minio://lab-reports/lab-reports/LAB-SEED-0006.pdf',
       lab.id, '2026-01-02 12:15:00', admin.id, '2026-01-02 12:45:00',
       '2026-01-02 13:00:00'
FROM samples s
JOIN complaints c ON c.id = s.complaint_id
JOIN users lab ON lab.email = 'lab@aaharrakshak.dev'
JOIN users admin ON admin.email = 'admin@aaharrakshak.dev'
WHERE c.ticket_number = 'ARK-SEED-0006';

INSERT INTO lab_test_results (
    lab_report_id,
    parameter_name,
    test_method,
    permissible_limit,
    result_value,
    unit,
    compliant,
    remarks
)
SELECT r.id, 'Synthetic colour screen', 'Mock IS method', 'Not detected',
       'Detected', NULL, FALSE, 'Mock academic non-compliant result'
FROM lab_reports r
WHERE r.report_number = 'LAB-SEED-0006';
