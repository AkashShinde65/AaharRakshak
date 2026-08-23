CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(120) NOT NULL,
    email VARCHAR(160) UNIQUE,
    mobile_number VARCHAR(20) UNIQUE,
    status VARCHAR(40) NOT NULL,
    identity_verification_token VARCHAR(120),
    identity_verification_status VARCHAR(30),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE roles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(60) NOT NULL UNIQUE
);

CREATE TABLE user_roles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id),
    CONSTRAINT uq_user_roles UNIQUE (user_id, role_id)
);

CREATE TABLE companies (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    legal_name VARCHAR(180) NOT NULL,
    trade_name VARCHAR(180),
    gstin VARCHAR(30),
    status VARCHAR(40) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE licences (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    company_id BIGINT NOT NULL,
    licence_number VARCHAR(40) NOT NULL UNIQUE,
    issuing_authority VARCHAR(120),
    valid_from DATE,
    valid_to DATE,
    status VARCHAR(40) NOT NULL,
    CONSTRAINT fk_licences_company FOREIGN KEY (company_id) REFERENCES companies(id)
);

CREATE TABLE products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    company_id BIGINT NOT NULL,
    name VARCHAR(180) NOT NULL,
    barcode VARCHAR(80),
    category VARCHAR(80),
    CONSTRAINT fk_products_company FOREIGN KEY (company_id) REFERENCES companies(id)
);

CREATE TABLE batches (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL,
    batch_number VARCHAR(80) NOT NULL,
    manufactured_on DATE,
    expires_on DATE,
    CONSTRAINT fk_batches_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT uq_batches_product_number UNIQUE (product_id, batch_number)
);

CREATE TABLE complaints (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    ticket_number VARCHAR(40) NOT NULL UNIQUE,
    citizen_id BIGINT NOT NULL,
    company_id BIGINT,
    product_id BIGINT,
    batch_id BIGINT,
    category VARCHAR(60) NOT NULL,
    status VARCHAR(60) NOT NULL,
    description VARCHAR(1000),
    latitude DECIMAL(10, 7),
    longitude DECIMAL(10, 7),
    location_text VARCHAR(220),
    risk_score INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_complaints_citizen FOREIGN KEY (citizen_id) REFERENCES users(id),
    CONSTRAINT fk_complaints_company FOREIGN KEY (company_id) REFERENCES companies(id),
    CONSTRAINT fk_complaints_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_complaints_batch FOREIGN KEY (batch_id) REFERENCES batches(id)
);

CREATE TABLE evidence (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    complaint_id BIGINT NOT NULL,
    type VARCHAR(60) NOT NULL,
    object_key VARCHAR(500) NOT NULL,
    content_type VARCHAR(120),
    uploaded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_evidence_complaint FOREIGN KEY (complaint_id) REFERENCES complaints(id)
);

CREATE TABLE assignments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    complaint_id BIGINT NOT NULL,
    assigned_to_user_id BIGINT NOT NULL,
    assigned_by_user_id BIGINT NOT NULL,
    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    notes VARCHAR(500),
    CONSTRAINT fk_assignments_complaint FOREIGN KEY (complaint_id) REFERENCES complaints(id),
    CONSTRAINT fk_assignments_assigned_to FOREIGN KEY (assigned_to_user_id) REFERENCES users(id),
    CONSTRAINT fk_assignments_assigned_by FOREIGN KEY (assigned_by_user_id) REFERENCES users(id)
);

CREATE TABLE samples (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    complaint_id BIGINT NOT NULL,
    seal_number VARCHAR(80) NOT NULL UNIQUE,
    chain_of_custody VARCHAR(1000),
    collected_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_samples_complaint FOREIGN KEY (complaint_id) REFERENCES complaints(id)
);

CREATE TABLE lab_reports (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    sample_id BIGINT NOT NULL,
    report_number VARCHAR(80) NOT NULL,
    object_key VARCHAR(500) NOT NULL,
    result_summary VARCHAR(80),
    uploaded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_lab_reports_sample FOREIGN KEY (sample_id) REFERENCES samples(id)
);

CREATE TABLE actions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    complaint_id BIGINT NOT NULL,
    decided_by_user_id BIGINT NOT NULL,
    type VARCHAR(60) NOT NULL,
    summary VARCHAR(1000) NOT NULL,
    decided_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_actions_complaint FOREIGN KEY (complaint_id) REFERENCES complaints(id),
    CONSTRAINT fk_actions_decided_by FOREIGN KEY (decided_by_user_id) REFERENCES users(id)
);

CREATE TABLE notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    channel VARCHAR(80) NOT NULL,
    subject VARCHAR(180) NOT NULL,
    body VARCHAR(1000) NOT NULL,
    status VARCHAR(40) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE audit_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    actor_user_id BIGINT,
    action VARCHAR(120) NOT NULL,
    entity_type VARCHAR(80) NOT NULL,
    entity_id VARCHAR(80),
    details VARCHAR(2000),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_logs_actor FOREIGN KEY (actor_user_id) REFERENCES users(id)
);

INSERT INTO roles (name) VALUES
    ('CITIZEN'),
    ('COMPANY'),
    ('FOOD_INSPECTOR'),
    ('LABORATORY_OFFICER'),
    ('DISTRICT_ESCALATION_OFFICER'),
    ('CENTRAL_ADMINISTRATOR');

