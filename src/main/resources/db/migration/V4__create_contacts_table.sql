CREATE TABLE contact (
	id UUID PRIMARY KEY,
	name VARCHAR(255),
	email VARCHAR(255),
	secondary_email VARCHAR(255),
	phone_number VARCHAR(50),
	description TEXT,
	note TEXT,
	owner_id UUID NOT NULL,
	organization_id UUID NOT NULL,
	department VARCHAR(255),
	contact_source VARCHAR(255),
	title VARCHAR(255),
	address VARCHAR(255),
	country VARCHAR(255),
	state VARCHAR(255),
	city VARCHAR(255),
	created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT fk_contact_owner FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE,
	CONSTRAINT fk_contact_organization FOREIGN KEY (organization_id) REFERENCES organization(id) ON DELETE CASCADE
);

CREATE INDEX idx_contact_owner_id ON contact (owner_id);
CREATE INDEX idx_contact_organization_id ON contact (organization_id);
CREATE INDEX idx_contact_email ON contact (email);

