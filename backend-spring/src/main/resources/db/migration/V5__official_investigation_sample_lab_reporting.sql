ALTER TABLE complaints ADD COLUMN district VARCHAR(120);
ALTER TABLE complaints ADD COLUMN sla_due_at TIMESTAMP NULL;

CREATE TABLE inspection_visits (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    complaint_id BIGINT NOT NULL,
    inspector_user_id BIGINT NOT NULL,
    scheduled_by_user_id BIGINT NOT NULL,
    scheduled_at TIMESTAMP NOT NULL,
    status VARCHAR(40) NOT NULL,
    check_in_at TIMESTAMP NULL,
    check_in_latitude DECIMAL(10, 7),
    check_in_longitude DECIMAL(10, 7),
    location_text VARCHAR(220),
    visit_notes VARCHAR(1500),
    completed_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_inspection_visits_complaint FOREIGN KEY (complaint_id) REFERENCES complaints(id),
    CONSTRAINT fk_inspection_visits_inspector FOREIGN KEY (inspector_user_id) REFERENCES users(id),
    CONSTRAINT fk_inspection_visits_scheduled_by FOREIGN KEY (scheduled_by_user_id) REFERENCES users(id)
);

CREATE TABLE inspection_evidence (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    inspection_visit_id BIGINT NOT NULL,
    type VARCHAR(60) NOT NULL,
    object_key VARCHAR(500) NOT NULL,
    original_file_name VARCHAR(180),
    content_type VARCHAR(120),
    file_size_bytes BIGINT,
    checksum_sha256 VARCHAR(64) NOT NULL,
    captured_at TIMESTAMP NULL,
    uploaded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    storage_uri VARCHAR(600),
    CONSTRAINT fk_inspection_evidence_visit FOREIGN KEY (inspection_visit_id) REFERENCES inspection_visits(id)
);

ALTER TABLE samples ADD COLUMN inspection_visit_id BIGINT;
ALTER TABLE samples ADD COLUMN collected_by_user_id BIGINT;
ALTER TABLE samples ADD COLUMN sample_number VARCHAR(80);
ALTER TABLE samples ADD COLUMN quantity VARCHAR(80);
ALTER TABLE samples ADD COLUMN latitude DECIMAL(10, 7);
ALTER TABLE samples ADD COLUMN longitude DECIMAL(10, 7);
ALTER TABLE samples ADD COLUMN location_text VARCHAR(220);
ALTER TABLE samples ADD COLUMN storage_details VARCHAR(500);
ALTER TABLE samples ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE samples ADD CONSTRAINT fk_samples_inspection_visit FOREIGN KEY (inspection_visit_id) REFERENCES inspection_visits(id);
ALTER TABLE samples ADD CONSTRAINT fk_samples_collected_by FOREIGN KEY (collected_by_user_id) REFERENCES users(id);

CREATE UNIQUE INDEX uq_samples_sample_number ON samples (sample_number);
CREATE INDEX idx_inspection_visits_complaint ON inspection_visits (complaint_id, scheduled_at);
CREATE INDEX idx_inspection_evidence_visit ON inspection_evidence (inspection_visit_id, uploaded_at);
CREATE INDEX idx_samples_complaint_collected ON samples (complaint_id, collected_at);

CREATE TABLE sample_chain_of_custody (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    sample_id BIGINT NOT NULL,
    event_type VARCHAR(60) NOT NULL,
    actor_user_id BIGINT NOT NULL,
    from_user_id BIGINT,
    to_user_id BIGINT,
    location_text VARCHAR(220),
    notes VARCHAR(500),
    event_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sample_custody_sample FOREIGN KEY (sample_id) REFERENCES samples(id),
    CONSTRAINT fk_sample_custody_actor FOREIGN KEY (actor_user_id) REFERENCES users(id),
    CONSTRAINT fk_sample_custody_from_user FOREIGN KEY (from_user_id) REFERENCES users(id),
    CONSTRAINT fk_sample_custody_to_user FOREIGN KEY (to_user_id) REFERENCES users(id)
);

CREATE TABLE sample_lab_assignments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    sample_id BIGINT NOT NULL,
    assigned_to_user_id BIGINT NOT NULL,
    assigned_by_user_id BIGINT NOT NULL,
    status VARCHAR(40) NOT NULL,
    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    received_at TIMESTAMP NULL,
    received_by_user_id BIGINT,
    notes VARCHAR(500),
    CONSTRAINT fk_sample_lab_assignments_sample FOREIGN KEY (sample_id) REFERENCES samples(id),
    CONSTRAINT fk_sample_lab_assignments_assigned_to FOREIGN KEY (assigned_to_user_id) REFERENCES users(id),
    CONSTRAINT fk_sample_lab_assignments_assigned_by FOREIGN KEY (assigned_by_user_id) REFERENCES users(id),
    CONSTRAINT fk_sample_lab_assignments_received_by FOREIGN KEY (received_by_user_id) REFERENCES users(id)
);

ALTER TABLE lab_reports ADD COLUMN status VARCHAR(40) NOT NULL DEFAULT 'DRAFT';
ALTER TABLE lab_reports ADD COLUMN original_file_name VARCHAR(180);
ALTER TABLE lab_reports ADD COLUMN content_type VARCHAR(120);
ALTER TABLE lab_reports ADD COLUMN file_size_bytes BIGINT;
ALTER TABLE lab_reports ADD COLUMN checksum_sha256 VARCHAR(64);
ALTER TABLE lab_reports ADD COLUMN storage_uri VARCHAR(600);
ALTER TABLE lab_reports ADD COLUMN submitted_by_user_id BIGINT;
ALTER TABLE lab_reports ADD COLUMN submitted_at TIMESTAMP NULL;
ALTER TABLE lab_reports ADD COLUMN reviewed_by_user_id BIGINT;
ALTER TABLE lab_reports ADD COLUMN reviewed_at TIMESTAMP NULL;
ALTER TABLE lab_reports ADD COLUMN published_at TIMESTAMP NULL;
ALTER TABLE lab_reports ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE lab_reports ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE lab_reports ADD CONSTRAINT fk_lab_reports_submitted_by FOREIGN KEY (submitted_by_user_id) REFERENCES users(id);
ALTER TABLE lab_reports ADD CONSTRAINT fk_lab_reports_reviewed_by FOREIGN KEY (reviewed_by_user_id) REFERENCES users(id);

CREATE TABLE lab_test_results (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    lab_report_id BIGINT NOT NULL,
    parameter_name VARCHAR(120) NOT NULL,
    test_method VARCHAR(120),
    permissible_limit VARCHAR(80),
    result_value VARCHAR(80) NOT NULL,
    unit VARCHAR(40),
    compliant BOOLEAN NOT NULL,
    remarks VARCHAR(500),
    CONSTRAINT fk_lab_test_results_report FOREIGN KEY (lab_report_id) REFERENCES lab_reports(id)
);

CREATE INDEX idx_sample_custody_sample ON sample_chain_of_custody (sample_id, event_at);
CREATE INDEX idx_sample_lab_assignments_lab ON sample_lab_assignments (assigned_to_user_id, assigned_at);
CREATE INDEX idx_lab_reports_sample ON lab_reports (sample_id, uploaded_at);
CREATE INDEX idx_lab_test_results_report ON lab_test_results (lab_report_id);

UPDATE complaints
SET district = 'Pune',
    sla_due_at = '2026-01-04 09:00:00'
WHERE ticket_number = 'ARK-SEED-0001';

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
    'ARK-SEED-0005',
    citizen.id,
    company.id,
    product.id,
    batch.id,
    'PACKAGED_FOOD',
    'SUSPECTED_ADULTERATION',
    'SAMPLE_COLLECTED',
    'Seeded Phase 5 complaint with inspection and sealed sample ready for laboratory receipt.',
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
    70,
    'Pune',
    '2026-01-04 09:00:00',
    '2026-01-01 09:00:00'
FROM users citizen
JOIN companies company ON company.legal_name = 'Demo Foods Private Limited'
JOIN products product ON product.company_id = company.id AND product.barcode = '8901234567890'
JOIN batches batch ON batch.product_id = product.id AND batch.batch_number = 'TUR-2026-001'
WHERE citizen.email = 'citizen@aaharrakshak.dev';

INSERT INTO complaint_status_history (complaint_id, status, changed_by_user_id, notes)
SELECT c.id, 'DRAFT', citizen.id, 'Seeded Phase 5 draft'
FROM complaints c
JOIN users citizen ON citizen.email = 'citizen@aaharrakshak.dev'
WHERE c.ticket_number = 'ARK-SEED-0005';

INSERT INTO complaint_status_history (complaint_id, status, changed_by_user_id, notes)
SELECT c.id, 'SUBMITTED', citizen.id, 'Seeded Phase 5 complaint submitted'
FROM complaints c
JOIN users citizen ON citizen.email = 'citizen@aaharrakshak.dev'
WHERE c.ticket_number = 'ARK-SEED-0005';

INSERT INTO complaint_status_history (complaint_id, status, changed_by_user_id, notes)
SELECT c.id, 'VERIFIED', admin.id, 'Seeded Phase 5 complaint verified'
FROM complaints c
JOIN users admin ON admin.email = 'admin@aaharrakshak.dev'
WHERE c.ticket_number = 'ARK-SEED-0005';

INSERT INTO complaint_status_history (complaint_id, status, changed_by_user_id, notes)
SELECT c.id, 'ASSIGNED', admin.id, 'Seeded Phase 5 complaint assigned to inspector'
FROM complaints c
JOIN users admin ON admin.email = 'admin@aaharrakshak.dev'
WHERE c.ticket_number = 'ARK-SEED-0005';

INSERT INTO complaint_status_history (complaint_id, status, changed_by_user_id, notes)
SELECT c.id, 'INSPECTION_SCHEDULED', inspector.id, 'Seeded Phase 5 inspection scheduled'
FROM complaints c
JOIN users inspector ON inspector.email = 'inspector@aaharrakshak.dev'
WHERE c.ticket_number = 'ARK-SEED-0005';

INSERT INTO complaint_status_history (complaint_id, status, changed_by_user_id, notes)
SELECT c.id, 'SAMPLE_COLLECTED', inspector.id, 'Seeded Phase 5 sample collected'
FROM complaints c
JOIN users inspector ON inspector.email = 'inspector@aaharrakshak.dev'
WHERE c.ticket_number = 'ARK-SEED-0005';

INSERT INTO assignments (
    complaint_id,
    assigned_to_user_id,
    assigned_by_user_id,
    assigned_at,
    notes
)
SELECT c.id, inspector.id, admin.id, '2026-01-01 09:15:00', 'Seeded Phase 5 assignment'
FROM complaints c
JOIN users inspector ON inspector.email = 'inspector@aaharrakshak.dev'
JOIN users admin ON admin.email = 'admin@aaharrakshak.dev'
WHERE c.ticket_number = 'ARK-SEED-0005';

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
SELECT c.id, inspector.id, admin.id, '2026-01-02 09:00:00', 'COMPLETED',
       '2026-01-02 09:05:00', 18.5204300, 73.8567400, 'Pune demo market',
       'Seeded mock inspection visit. Visual evidence is not proof of adulteration.',
       '2026-01-02 09:45:00'
FROM complaints c
JOIN users inspector ON inspector.email = 'inspector@aaharrakshak.dev'
JOIN users admin ON admin.email = 'admin@aaharrakshak.dev'
WHERE c.ticket_number = 'ARK-SEED-0005';

INSERT INTO inspection_evidence (
    inspection_visit_id,
    type,
    object_key,
    original_file_name,
    content_type,
    file_size_bytes,
    checksum_sha256,
    captured_at,
    storage_uri
)
SELECT v.id, 'FOOD_PHOTO', 'demo/inspections/ARK-SEED-0005/food.jpg', 'food.jpg',
       'image/jpeg', 204800, 'dddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd',
       '2026-01-02 09:10:00',
       'local-mock-minio://inspection-evidence/demo/inspections/ARK-SEED-0005/food.jpg'
FROM inspection_visits v
JOIN complaints c ON c.id = v.complaint_id
WHERE c.ticket_number = 'ARK-SEED-0005';

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
SELECT c.id, v.id, inspector.id, 'SMP-SEED-0005', 'SEAL-SEED-0005', '250 g',
       'Seeded sample collected and sealed for mock laboratory workflow.',
       '2026-01-02 09:50:00', 18.5204300, 73.8567400,
       'Pune demo market', 'Sterile container, cold-box slot A2'
FROM complaints c
JOIN inspection_visits v ON v.complaint_id = c.id
JOIN users inspector ON inspector.email = 'inspector@aaharrakshak.dev'
WHERE c.ticket_number = 'ARK-SEED-0005';

INSERT INTO sample_chain_of_custody (
    sample_id,
    event_type,
    actor_user_id,
    to_user_id,
    location_text,
    notes,
    event_at
)
SELECT s.id, 'COLLECTED', inspector.id, inspector.id, 'Pune demo market',
       'Seeded sample collected and sealed', '2026-01-02 09:50:00'
FROM samples s
JOIN complaints c ON c.id = s.complaint_id
JOIN users inspector ON inspector.email = 'inspector@aaharrakshak.dev'
WHERE c.ticket_number = 'ARK-SEED-0005';

INSERT INTO sample_lab_assignments (
    sample_id,
    assigned_to_user_id,
    assigned_by_user_id,
    status,
    assigned_at,
    notes
)
SELECT s.id, lab.id, admin.id, 'ASSIGNED', '2026-01-02 10:15:00',
       'Seeded sample assigned to demo laboratory officer'
FROM samples s
JOIN complaints c ON c.id = s.complaint_id
JOIN users lab ON lab.email = 'lab@aaharrakshak.dev'
JOIN users admin ON admin.email = 'admin@aaharrakshak.dev'
WHERE c.ticket_number = 'ARK-SEED-0005';

INSERT INTO sample_chain_of_custody (
    sample_id,
    event_type,
    actor_user_id,
    from_user_id,
    to_user_id,
    location_text,
    notes,
    event_at
)
SELECT s.id, 'TRANSFERRED_TO_LAB', admin.id, inspector.id, lab.id, 'Pune demo market',
       'Seeded sample assigned to laboratory officer', '2026-01-02 10:15:00'
FROM samples s
JOIN complaints c ON c.id = s.complaint_id
JOIN users inspector ON inspector.email = 'inspector@aaharrakshak.dev'
JOIN users lab ON lab.email = 'lab@aaharrakshak.dev'
JOIN users admin ON admin.email = 'admin@aaharrakshak.dev'
WHERE c.ticket_number = 'ARK-SEED-0005';
