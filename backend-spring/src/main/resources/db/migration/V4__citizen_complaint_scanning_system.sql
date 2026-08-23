ALTER TABLE complaints ADD COLUMN complaint_type VARCHAR(40) NOT NULL DEFAULT 'PACKAGED_FOOD';
ALTER TABLE complaints ADD COLUMN scanned_barcode VARCHAR(32);
ALTER TABLE complaints ADD COLUMN detected_product_name VARCHAR(180);
ALTER TABLE complaints ADD COLUMN detected_company_name VARCHAR(180);
ALTER TABLE complaints ADD COLUMN detected_fssai_licence_number VARCHAR(14);
ALTER TABLE complaints ADD COLUMN detected_batch_number VARCHAR(80);
ALTER TABLE complaints ADD COLUMN detected_expiry_date DATE;
ALTER TABLE complaints ADD COLUMN confirmed_product_name VARCHAR(180);
ALTER TABLE complaints ADD COLUMN confirmed_company_name VARCHAR(180);
ALTER TABLE complaints ADD COLUMN confirmed_fssai_licence_number VARCHAR(14);
ALTER TABLE complaints ADD COLUMN confirmed_batch_number VARCHAR(80);
ALTER TABLE complaints ADD COLUMN confirmed_expiry_date DATE;
ALTER TABLE complaints ADD COLUMN vendor_name VARCHAR(180);
ALTER TABLE complaints ADD COLUMN vendor_address VARCHAR(300);
ALTER TABLE complaints ADD COLUMN gps_consent BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE complaints ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE complaints ADD COLUMN submitted_at TIMESTAMP NULL;

ALTER TABLE evidence ADD COLUMN original_file_name VARCHAR(180);
ALTER TABLE evidence ADD COLUMN file_size_bytes BIGINT;
ALTER TABLE evidence ADD COLUMN checksum_sha256 VARCHAR(64) NOT NULL DEFAULT '0000000000000000000000000000000000000000000000000000000000000000';
ALTER TABLE evidence ADD COLUMN captured_at TIMESTAMP NULL;
ALTER TABLE evidence ADD COLUMN storage_uri VARCHAR(600);

CREATE TABLE complaint_status_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    complaint_id BIGINT NOT NULL,
    status VARCHAR(60) NOT NULL,
    changed_by_user_id BIGINT,
    notes VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_complaint_status_history_complaint FOREIGN KEY (complaint_id) REFERENCES complaints(id),
    CONSTRAINT fk_complaint_status_history_user FOREIGN KEY (changed_by_user_id) REFERENCES users(id)
);

CREATE INDEX idx_complaints_citizen_created ON complaints (citizen_id, created_at);
CREATE INDEX idx_complaints_ticket ON complaints (ticket_number);
CREATE INDEX idx_complaint_status_history_complaint ON complaint_status_history (complaint_id, created_at);

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
    submitted_at
)
SELECT
    'ARK-SEED-0001',
    citizen.id,
    company.id,
    product.id,
    batch.id,
    'PACKAGED_FOOD',
    'SUSPECTED_ADULTERATION',
    'ASSIGNED',
    'Seeded mock packaged-food complaint for Phase 4 assignment testing. Image evidence is not proof of adulteration.',
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
    25,
    CURRENT_TIMESTAMP
FROM users citizen
JOIN companies company ON company.legal_name = 'Demo Foods Private Limited'
JOIN products product ON product.company_id = company.id AND product.barcode = '8901234567890'
JOIN batches batch ON batch.product_id = product.id AND batch.batch_number = 'TUR-2026-001'
WHERE citizen.email = 'citizen@aaharrakshak.dev';

INSERT INTO evidence (
    complaint_id,
    type,
    object_key,
    original_file_name,
    content_type,
    file_size_bytes,
    checksum_sha256,
    captured_at,
    storage_uri
)
SELECT
    c.id,
    'PRODUCT_LABEL_PHOTO',
    'demo/complaints/ARK-SEED-0001/product-label.jpg',
    'product-label.jpg',
    'image/jpeg',
    1048576,
    'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa',
    CURRENT_TIMESTAMP,
    'local-mock-minio://complaint-evidence/demo/complaints/ARK-SEED-0001/product-label.jpg'
FROM complaints c
WHERE c.ticket_number = 'ARK-SEED-0001';

INSERT INTO complaint_status_history (complaint_id, status, changed_by_user_id, notes)
SELECT c.id, 'DRAFT', citizen.id, 'Seeded draft created'
FROM complaints c
JOIN users citizen ON citizen.email = 'citizen@aaharrakshak.dev'
WHERE c.ticket_number = 'ARK-SEED-0001';

INSERT INTO complaint_status_history (complaint_id, status, changed_by_user_id, notes)
SELECT c.id, 'SUBMITTED', citizen.id, 'Seeded complaint submitted'
FROM complaints c
JOIN users citizen ON citizen.email = 'citizen@aaharrakshak.dev'
WHERE c.ticket_number = 'ARK-SEED-0001';

INSERT INTO complaint_status_history (complaint_id, status, changed_by_user_id, notes)
SELECT c.id, 'ASSIGNED', admin.id, 'Seeded complaint assigned to food inspector'
FROM complaints c
JOIN users admin ON admin.email = 'admin@aaharrakshak.dev'
WHERE c.ticket_number = 'ARK-SEED-0001';

INSERT INTO assignments (
    complaint_id,
    assigned_to_user_id,
    assigned_by_user_id,
    notes
)
SELECT c.id, inspector.id, admin.id, 'Seeded Phase 4 assignment'
FROM complaints c
JOIN users inspector ON inspector.email = 'inspector@aaharrakshak.dev'
JOIN users admin ON admin.email = 'admin@aaharrakshak.dev'
WHERE c.ticket_number = 'ARK-SEED-0001';
