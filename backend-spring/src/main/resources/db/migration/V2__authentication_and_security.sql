ALTER TABLE users ADD COLUMN password_hash VARCHAR(255);
ALTER TABLE users ADD COLUMN failed_login_attempts INT NOT NULL DEFAULT 0;
ALTER TABLE users ADD COLUMN locked_until TIMESTAMP NULL;
ALTER TABLE users ADD COLUMN last_login_at TIMESTAMP NULL;
ALTER TABLE users ADD COLUMN email_verified BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE users ADD COLUMN mobile_verified BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE companies ADD COLUMN owner_user_id BIGINT NULL;
ALTER TABLE companies ADD CONSTRAINT fk_companies_owner_user FOREIGN KEY (owner_user_id) REFERENCES users(id);

CREATE TABLE refresh_tokens (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    token_hash VARCHAR(128) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    revoked_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE otp_verifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    channel VARCHAR(20) NOT NULL,
    destination VARCHAR(160) NOT NULL,
    code VARCHAR(12) NOT NULL,
    verified BOOLEAN NOT NULL DEFAULT FALSE,
    expires_at TIMESTAMP NOT NULL,
    verified_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_otp_verifications_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_otp_destination_channel ON otp_verifications (destination, channel, verified, created_at);

INSERT INTO users (
    full_name,
    email,
    mobile_number,
    status,
    password_hash,
    identity_verification_status,
    email_verified,
    mobile_verified
) VALUES
    ('Demo Citizen', 'citizen@aaharrakshak.dev', '9000000001', 'ACTIVE', '$2y$10$kPl4f.fV33jVEaCDTNvVe.lPFMyI85I2aYFEsQpI5JZV4IGKxE58y', 'MOCK_AADHAAR_VERIFIED', TRUE, TRUE),
    ('Demo Company User', 'company@aaharrakshak.dev', '9000000002', 'ACTIVE', '$2y$10$kPl4f.fV33jVEaCDTNvVe.lPFMyI85I2aYFEsQpI5JZV4IGKxE58y', 'UNVERIFIED', TRUE, TRUE),
    ('Demo Food Inspector', 'inspector@aaharrakshak.dev', '9000000003', 'ACTIVE', '$2y$10$kPl4f.fV33jVEaCDTNvVe.lPFMyI85I2aYFEsQpI5JZV4IGKxE58y', 'OFFICIAL_VERIFIED', TRUE, TRUE),
    ('Demo Lab Officer', 'lab@aaharrakshak.dev', '9000000004', 'ACTIVE', '$2y$10$kPl4f.fV33jVEaCDTNvVe.lPFMyI85I2aYFEsQpI5JZV4IGKxE58y', 'OFFICIAL_VERIFIED', TRUE, TRUE),
    ('Demo District Officer', 'district@aaharrakshak.dev', '9000000005', 'ACTIVE', '$2y$10$kPl4f.fV33jVEaCDTNvVe.lPFMyI85I2aYFEsQpI5JZV4IGKxE58y', 'OFFICIAL_VERIFIED', TRUE, TRUE),
    ('Demo Admin', 'admin@aaharrakshak.dev', '9000000006', 'ACTIVE', '$2y$10$kPl4f.fV33jVEaCDTNvVe.lPFMyI85I2aYFEsQpI5JZV4IGKxE58y', 'OFFICIAL_VERIFIED', TRUE, TRUE);

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.name = 'CITIZEN'
WHERE u.email = 'citizen@aaharrakshak.dev';

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.name = 'COMPANY'
WHERE u.email = 'company@aaharrakshak.dev';

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.name = 'FOOD_INSPECTOR'
WHERE u.email = 'inspector@aaharrakshak.dev';

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.name = 'LABORATORY_OFFICER'
WHERE u.email = 'lab@aaharrakshak.dev';

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.name = 'DISTRICT_ESCALATION_OFFICER'
WHERE u.email = 'district@aaharrakshak.dev';

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.name = 'CENTRAL_ADMINISTRATOR'
WHERE u.email = 'admin@aaharrakshak.dev';

INSERT INTO companies (legal_name, trade_name, gstin, status, owner_user_id)
SELECT 'Demo Foods Private Limited', 'Demo Foods', '27ABCDE1234F1Z5', 'PENDING_VERIFICATION', u.id
FROM users u
WHERE u.email = 'company@aaharrakshak.dev';
