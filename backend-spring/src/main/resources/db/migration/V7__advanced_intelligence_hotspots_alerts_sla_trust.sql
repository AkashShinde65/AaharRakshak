CREATE TABLE complaint_hotspots (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    hotspot_key VARCHAR(160) NOT NULL UNIQUE,
    district VARCHAR(120) NOT NULL,
    related_key VARCHAR(220) NOT NULL,
    product_or_vendor VARCHAR(180) NOT NULL,
    risk_level VARCHAR(40) NOT NULL,
    complaint_count INT NOT NULL,
    radius_km DECIMAL(8, 3) NOT NULL,
    center_latitude DECIMAL(10, 7) NOT NULL,
    center_longitude DECIMAL(10, 7) NOT NULL,
    window_start TIMESTAMP NOT NULL,
    window_end TIMESTAMP NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    detected_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE complaint_hotspot_members (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    hotspot_id BIGINT NOT NULL,
    complaint_id BIGINT NOT NULL,
    CONSTRAINT fk_hotspot_members_hotspot FOREIGN KEY (hotspot_id) REFERENCES complaint_hotspots(id),
    CONSTRAINT fk_hotspot_members_complaint FOREIGN KEY (complaint_id) REFERENCES complaints(id)
);

CREATE TABLE alert_outbox (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    event_type VARCHAR(80) NOT NULL,
    channel VARCHAR(40) NOT NULL,
    subject VARCHAR(180) NOT NULL,
    body VARCHAR(1200) NOT NULL,
    payload_json VARCHAR(3000),
    location_text VARCHAR(220),
    company_id BIGINT,
    product_id BIGINT,
    batch_id BIGINT,
    status VARCHAR(40) NOT NULL,
    retry_count INT NOT NULL DEFAULT 0,
    next_attempt_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    sent_at TIMESTAMP NULL,
    last_error VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_alert_outbox_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_alert_outbox_company FOREIGN KEY (company_id) REFERENCES companies(id),
    CONSTRAINT fk_alert_outbox_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_alert_outbox_batch FOREIGN KEY (batch_id) REFERENCES batches(id)
);

CREATE TABLE sla_escalations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    complaint_id BIGINT NOT NULL,
    assigned_inspector_user_id BIGINT,
    escalated_to_user_id BIGINT NOT NULL,
    previous_status VARCHAR(60) NOT NULL,
    reason VARCHAR(1000) NOT NULL,
    escalated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    acknowledged_at TIMESTAMP NULL,
    CONSTRAINT fk_sla_escalations_complaint FOREIGN KEY (complaint_id) REFERENCES complaints(id),
    CONSTRAINT fk_sla_escalations_inspector FOREIGN KEY (assigned_inspector_user_id) REFERENCES users(id),
    CONSTRAINT fk_sla_escalations_officer FOREIGN KEY (escalated_to_user_id) REFERENCES users(id)
);

CREATE TABLE vendor_reviews (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    citizen_id BIGINT NOT NULL,
    company_id BIGINT NOT NULL,
    product_id BIGINT,
    batch_id BIGINT,
    rating INT NOT NULL,
    review_text VARCHAR(1000),
    receipt_object_key VARCHAR(500) NOT NULL,
    receipt_file_name VARCHAR(180) NOT NULL,
    receipt_content_type VARCHAR(120) NOT NULL,
    receipt_size_bytes BIGINT NOT NULL,
    receipt_checksum_sha256 VARCHAR(64) NOT NULL,
    receipt_verified BOOLEAN NOT NULL,
    receipt_verification_token VARCHAR(120) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_vendor_reviews_citizen FOREIGN KEY (citizen_id) REFERENCES users(id),
    CONSTRAINT fk_vendor_reviews_company FOREIGN KEY (company_id) REFERENCES companies(id),
    CONSTRAINT fk_vendor_reviews_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_vendor_reviews_batch FOREIGN KEY (batch_id) REFERENCES batches(id),
    CONSTRAINT uq_vendor_review_receipt UNIQUE (citizen_id, company_id, receipt_checksum_sha256)
);

CREATE TABLE vendor_trust_scores (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    company_id BIGINT NOT NULL UNIQUE,
    score DECIMAL(5, 2) NOT NULL,
    risk_level VARCHAR(40) NOT NULL,
    inspection_points DECIMAL(5, 2) NOT NULL,
    lab_points DECIMAL(5, 2) NOT NULL,
    recall_points DECIMAL(5, 2) NOT NULL,
    review_points DECIMAL(5, 2) NOT NULL,
    review_count INT NOT NULL,
    explanation VARCHAR(1600) NOT NULL,
    recalculated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_vendor_trust_scores_company FOREIGN KEY (company_id) REFERENCES companies(id)
);

CREATE TABLE risk_analyses (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    complaint_id BIGINT NOT NULL,
    score INT NOT NULL,
    risk_level VARCHAR(40) NOT NULL,
    reasons VARCHAR(2000) NOT NULL,
    adapter_name VARCHAR(120) NOT NULL,
    image_safety_note VARCHAR(500) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_risk_analyses_complaint FOREIGN KEY (complaint_id) REFERENCES complaints(id)
);

CREATE TABLE mock_external_events (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    event_type VARCHAR(80) NOT NULL,
    target_type VARCHAR(80) NOT NULL,
    target_id VARCHAR(120) NOT NULL,
    payload_json VARCHAR(3000) NOT NULL,
    status VARCHAR(40) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_hotspots_district ON complaint_hotspots (district, risk_level, detected_at);
CREATE INDEX idx_hotspot_members_hotspot ON complaint_hotspot_members (hotspot_id);
CREATE INDEX idx_alert_outbox_status ON alert_outbox (status, next_attempt_at);
CREATE INDEX idx_alert_outbox_user ON alert_outbox (user_id, created_at);
CREATE INDEX idx_sla_escalations_complaint ON sla_escalations (complaint_id, escalated_at);
CREATE INDEX idx_vendor_reviews_company ON vendor_reviews (company_id, created_at);
CREATE INDEX idx_risk_analyses_complaint ON risk_analyses (complaint_id, created_at);
CREATE INDEX idx_mock_external_events_type ON mock_external_events (event_type, created_at);

UPDATE complaints
SET sla_due_at = '2026-12-31 09:00:00'
WHERE ticket_number = 'ARK-SEED-0005';

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
    seed.ticket_number,
    citizen.id,
    company.id,
    product.id,
    batch.id,
    'PACKAGED_FOOD',
    'FOOD_POISONING_SYMPTOMS',
    'SUBMITTED',
    'Seeded Phase 7 clustered complaint for hotspot detection. It is an allegation until inspection and lab confirmation.',
    '8901234567890',
    'Demo Turmeric Powder',
    'Demo Foods Private Limited',
    '12345678901234',
    'TUR-2026-001',
    batch.expires_on,
    seed.latitude,
    seed.longitude,
    'Pune demo market cluster',
    TRUE,
    72,
    'Pune',
    '2026-12-31 09:00:00',
    CURRENT_TIMESTAMP
FROM (
    SELECT 'ARK-HOT-0001' AS ticket_number, 18.5200100 AS latitude, 73.8560100 AS longitude UNION ALL
    SELECT 'ARK-HOT-0002', 18.5200900, 73.8561200 UNION ALL
    SELECT 'ARK-HOT-0003', 18.5201700, 73.8562000 UNION ALL
    SELECT 'ARK-HOT-0004', 18.5202600, 73.8563100 UNION ALL
    SELECT 'ARK-HOT-0005', 18.5203400, 73.8563900 UNION ALL
    SELECT 'ARK-HOT-0006', 18.5204300, 73.8564700 UNION ALL
    SELECT 'ARK-HOT-0007', 18.5205100, 73.8565600 UNION ALL
    SELECT 'ARK-HOT-0008', 18.5206000, 73.8566400 UNION ALL
    SELECT 'ARK-HOT-0009', 18.5206900, 73.8567300 UNION ALL
    SELECT 'ARK-HOT-0010', 18.5207700, 73.8568200
) seed
JOIN users citizen ON citizen.email = 'citizen@aaharrakshak.dev'
JOIN companies company ON company.legal_name = 'Demo Foods Private Limited'
JOIN products product ON product.company_id = company.id AND product.barcode = '8901234567890'
JOIN batches batch ON batch.product_id = product.id AND batch.batch_number = 'TUR-2026-001';

INSERT INTO complaint_status_history (complaint_id, status, changed_by_user_id, notes)
SELECT c.id, 'SUBMITTED', citizen.id, 'Seeded Phase 7 hotspot complaint'
FROM complaints c
JOIN users citizen ON citizen.email = 'citizen@aaharrakshak.dev'
WHERE c.ticket_number LIKE 'ARK-HOT-00%';

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
    'ARK-SLA-0007',
    citizen.id,
    company.id,
    product.id,
    batch.id,
    'PACKAGED_FOOD',
    'FOOD_POISONING_SYMPTOMS',
    'ASSIGNED',
    'Seeded overdue high-risk complaint for SLA escalation.',
    '8901234567890',
    'Demo Turmeric Powder',
    'Demo Foods Private Limited',
    '12345678901234',
    'TUR-2026-001',
    batch.expires_on,
    18.5204300,
    73.8567400,
    'Pune demo market',
    TRUE,
    95,
    'Pune',
    '2026-01-01 09:00:00',
    '2026-01-01 08:00:00'
FROM users citizen
JOIN companies company ON company.legal_name = 'Demo Foods Private Limited'
JOIN products product ON product.company_id = company.id AND product.barcode = '8901234567890'
JOIN batches batch ON batch.product_id = product.id AND batch.batch_number = 'TUR-2026-001'
WHERE citizen.email = 'citizen@aaharrakshak.dev';

INSERT INTO complaint_status_history (complaint_id, status, changed_by_user_id, notes)
SELECT c.id, 'ASSIGNED', admin.id, 'Seeded Phase 7 overdue high-risk assignment'
FROM complaints c
JOIN users admin ON admin.email = 'admin@aaharrakshak.dev'
WHERE c.ticket_number = 'ARK-SLA-0007';

INSERT INTO assignments (complaint_id, assigned_to_user_id, assigned_by_user_id, assigned_at, notes)
SELECT c.id, inspector.id, admin.id, '2026-01-01 08:15:00', 'Seeded Phase 7 overdue assignment'
FROM complaints c
JOIN users inspector ON inspector.email = 'inspector@aaharrakshak.dev'
JOIN users admin ON admin.email = 'admin@aaharrakshak.dev'
WHERE c.ticket_number = 'ARK-SLA-0007';
