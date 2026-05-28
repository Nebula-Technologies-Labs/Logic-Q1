CREATE TABLE organization_invites (
	id UUID PRIMARY KEY,
	token VARCHAR(255) NOT NULL,
	email VARCHAR(255) NOT NULL,
	role VARCHAR(32) NOT NULL,
	organization_id UUID NOT NULL,
	invited_by UUID NOT NULL,
	status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
	expires_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
	created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT uk_organization_invites_token UNIQUE (token),
	CONSTRAINT ck_organization_invites_role CHECK (role IN ('SUPER_ADMIN', 'ORG_ADMIN', 'MEMBER')),
	CONSTRAINT ck_organization_invites_status CHECK (status IN ('PENDING', 'ACCEPTED', 'EXPIRED', 'REVOKED')),
	CONSTRAINT fk_organization_invites_organization FOREIGN KEY (organization_id) REFERENCES organization(id) ON DELETE CASCADE,
	CONSTRAINT fk_organization_invites_invited_by FOREIGN KEY (invited_by) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_organization_invites_organization_id ON organization_invites (organization_id);
CREATE INDEX idx_organization_invites_invited_by ON organization_invites (invited_by);
CREATE INDEX idx_organization_invites_email ON organization_invites (email);
CREATE INDEX idx_organization_invites_status ON organization_invites (status);
CREATE INDEX idx_organization_invites_expires_at ON organization_invites (expires_at);
CREATE UNIQUE INDEX uk_organization_invites_pending_email_org
	ON organization_invites (organization_id, email)
	WHERE status = 'PENDING';

