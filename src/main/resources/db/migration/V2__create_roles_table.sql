CREATE TABLE roles (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    role SMALLINT NOT NULL,
    description TEXT,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_roles_organization_role UNIQUE (organization_id, role),
    CONSTRAINT fk_roles_organization FOREIGN KEY (organization_id) REFERENCES organization(id) ON DELETE CASCADE
);
CREATE INDEX idx_roles_organization_id ON roles (organization_id);
