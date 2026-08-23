ALTER TABLE companies ADD COLUMN registered_address VARCHAR(300);
ALTER TABLE companies ADD COLUMN contact_email VARCHAR(160);
ALTER TABLE companies ADD COLUMN contact_mobile VARCHAR(20);
ALTER TABLE companies ADD COLUMN website_url VARCHAR(180);
ALTER TABLE companies ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE licences ADD COLUMN label_image_object_key VARCHAR(500);
ALTER TABLE licences ADD COLUMN label_image_file_name VARCHAR(180);
ALTER TABLE licences ADD COLUMN label_image_content_type VARCHAR(120);
ALTER TABLE licences ADD COLUMN label_image_size_bytes BIGINT;
ALTER TABLE licences ADD COLUMN registry_status VARCHAR(40);
ALTER TABLE licences ADD COLUMN registry_reference_token VARCHAR(120);
ALTER TABLE licences ADD COLUMN rejection_reason VARCHAR(500);
ALTER TABLE licences ADD COLUMN reviewed_by_user_id BIGINT;
ALTER TABLE licences ADD COLUMN reviewed_at TIMESTAMP NULL;
ALTER TABLE licences ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE licences ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE licences ADD CONSTRAINT fk_licences_reviewed_by FOREIGN KEY (reviewed_by_user_id) REFERENCES users(id);

ALTER TABLE products ADD COLUMN brand VARCHAR(120);
ALTER TABLE products ADD COLUMN manufacturer_name VARCHAR(180);
ALTER TABLE products ADD COLUMN description VARCHAR(1000);
ALTER TABLE products ADD COLUMN front_label_object_key VARCHAR(500);
ALTER TABLE products ADD COLUMN front_label_file_name VARCHAR(180);
ALTER TABLE products ADD COLUMN front_label_content_type VARCHAR(120);
ALTER TABLE products ADD COLUMN front_label_size_bytes BIGINT;
ALTER TABLE products ADD COLUMN licence_label_object_key VARCHAR(500);
ALTER TABLE products ADD COLUMN licence_label_file_name VARCHAR(180);
ALTER TABLE products ADD COLUMN licence_label_content_type VARCHAR(120);
ALTER TABLE products ADD COLUMN licence_label_size_bytes BIGINT;
ALTER TABLE products ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE products ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE batches ADD COLUMN status VARCHAR(40) NOT NULL DEFAULT 'ACTIVE';

CREATE TABLE product_barcodes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL,
    barcode VARCHAR(32) NOT NULL UNIQUE,
    barcode_type VARCHAR(20) NOT NULL,
    primary_code BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_product_barcodes_product FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE INDEX idx_products_name ON products (name);
CREATE INDEX idx_product_barcodes_product ON product_barcodes (product_id);

UPDATE companies
SET registered_address = 'Demo Industrial Estate, Pune, Maharashtra',
    contact_email = 'company@aaharrakshak.dev',
    contact_mobile = '9000000002',
    website_url = 'https://demo-foods.example.test'
WHERE legal_name = 'Demo Foods Private Limited';

INSERT INTO licences (
    company_id,
    licence_number,
    issuing_authority,
    valid_from,
    valid_to,
    status,
    label_image_object_key,
    label_image_file_name,
    label_image_content_type,
    label_image_size_bytes,
    registry_status,
    registry_reference_token
)
SELECT
    c.id,
    '12345678901234',
    'Mock FSSAI Licence Registry',
    '2025-01-01',
    '2028-12-31',
    'ACTIVE',
    'demo/licences/12345678901234-label.jpg',
    '12345678901234-label.jpg',
    'image/jpeg',
    204800,
    'VALID',
    'mock-fssai-901234'
FROM companies c
WHERE c.legal_name = 'Demo Foods Private Limited';

INSERT INTO products (
    company_id,
    name,
    barcode,
    category,
    brand,
    manufacturer_name,
    description,
    front_label_object_key,
    front_label_file_name,
    front_label_content_type,
    front_label_size_bytes,
    licence_label_object_key,
    licence_label_file_name,
    licence_label_content_type,
    licence_label_size_bytes
)
SELECT
    c.id,
    'Demo Turmeric Powder',
    '8901234567890',
    'Spices',
    'Demo Gold',
    'Demo Foods Private Limited',
    'Seeded mock product for barcode lookup and batch-management testing.',
    'demo/products/turmeric-front.jpg',
    'turmeric-front.jpg',
    'image/jpeg',
    307200,
    'demo/products/turmeric-licence-label.jpg',
    'turmeric-licence-label.jpg',
    'image/jpeg',
    153600
FROM companies c
WHERE c.legal_name = 'Demo Foods Private Limited';

INSERT INTO product_barcodes (product_id, barcode, barcode_type, primary_code)
SELECT p.id, p.barcode, 'GTIN_13', TRUE
FROM products p
WHERE p.barcode = '8901234567890';

INSERT INTO batches (
    product_id,
    batch_number,
    manufactured_on,
    expires_on,
    status
)
SELECT
    p.id,
    'TUR-2026-001',
    '2026-01-15',
    '2027-01-14',
    'ACTIVE'
FROM products p
WHERE p.barcode = '8901234567890';
