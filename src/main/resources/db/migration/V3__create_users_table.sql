CREATE TABLE users (
    id UUID PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone_number VARCHAR(50),
    profile_image TEXT,
    job_title VARCHAR(255),
    department VARCHAR(255),
    on_boarded BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    auth_by VARCHAR(50),
    created_by UUID,
    last_login_at TIMESTAMP WITHOUT TIME ZONE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    organization_id UUID,
    role_id UUID,
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT fk_users_organization FOREIGN KEY (organization_id) REFERENCES organization(id) ON DELETE CASCADE,
    CONSTRAINT fk_users_created_by FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE SET NULL
);

CREATE INDEX idx_users_organization_id ON users (organization_id);
CREATE INDEX idx_users_is_active ON users (is_active);
CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_users_created_by ON users (created_by);
CREATE INDEX idx_users_role_id ON users (role_id);

ALTER TABLE organization
    ADD CONSTRAINT fk_organization_admin_id
    FOREIGN KEY (admin_id) REFERENCES users(id) ON DELETE SET NULL;
