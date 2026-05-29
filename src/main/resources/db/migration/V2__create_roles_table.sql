CREATE TABLE roles (
    id UUID PRIMARY KEY,
    organization_id UUID,
    role SMALLINT NOT NULL,
    description TEXT,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_roles_organization_role UNIQUE (organization_id, role),
    CONSTRAINT fk_roles_organization FOREIGN KEY (organization_id) REFERENCES organization(id) ON DELETE CASCADE
);
CREATE INDEX idx_roles_organization_id ON roles (organization_id);
-- Default system roles
INSERT INTO roles (id, role, description, created_at)
VALUES
    ('11111111-1111-1111-1111-111111111111', 0, 'SUPER_ADMIN', CURRENT_TIMESTAMP),
    ('22222222-2222-2222-2222-222222222222', 1, 'ORG_ADMIN', CURRENT_TIMESTAMP),
    ('33333333-3333-3333-3333-333333333333', 2, 'MEMBER', CURRENT_TIMESTAMP);
