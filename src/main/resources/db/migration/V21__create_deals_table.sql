CREATE TABLE deals (
                       id UUID PRIMARY KEY,

                       deal_name VARCHAR(255) NOT NULL,

                       amount NUMERIC(15,2),

                       expected_revenue NUMERIC(15,2),

                       stage VARCHAR(50) NOT NULL,

                       type VARCHAR(50),

                       probability INTEGER CHECK (probability BETWEEN 0 AND 100),

                       next_step VARCHAR(255),

                       lead_source VARCHAR(100),

                       campaign_source VARCHAR(255),

                       closing_date DATE,

                       description TEXT,

                       contact_id UUID,

                       owner_id UUID NOT NULL,

                       organization_id UUID NOT NULL,

                       created_by UUID NOT NULL,

                       created_at TIMESTAMP NOT NULL,

                       updated_at TIMESTAMP NOT NULL,

                       CONSTRAINT fk_deals_contact
                           FOREIGN KEY (contact_id)
                               REFERENCES contact(id),

                       CONSTRAINT fk_deals_owner
                           FOREIGN KEY (owner_id)
                               REFERENCES users(id),

                       CONSTRAINT fk_deals_organization
                           FOREIGN KEY (organization_id)
                               REFERENCES organization(id),

                       CONSTRAINT fk_deals_created_by
                           FOREIGN KEY (created_by)
                               REFERENCES users(id)
);

CREATE INDEX idx_deals_org
    ON deals(organization_id);

CREATE INDEX idx_deals_owner
    ON deals(owner_id);

CREATE INDEX idx_deals_stage
    ON deals(stage);

CREATE INDEX idx_deals_contact
    ON deals(contact_id);