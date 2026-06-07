CREATE TABLE lead
(
    id                   UUID NOT NULL,
    organization_id      UUID NOT NULL,
    owner_id             UUID,
    title                VARCHAR(255),
    first_name           VARCHAR(255),
    last_name            VARCHAR(255),
    company              VARCHAR(255),
    email                VARCHAR(255),
    secondary_email      VARCHAR(255),
    phone                VARCHAR(255),
    mobile               VARCHAR(255),
    fax                  VARCHAR(255),
    website              VARCHAR(255),
    twitter_handle       VARCHAR(255),
    annual_revenue       DECIMAL,
    employee_count       INTEGER,
    email_opt_out        BOOLEAN,
    street               VARCHAR(255),
    city                 VARCHAR(255),
    state                VARCHAR(255),
    postal_code          VARCHAR(255),
    country              VARCHAR(255),
    description          TEXT,
    industry             VARCHAR(255),
    lead_status          VARCHAR(255),
    lead_source          VARCHAR(255),
    rating               VARCHAR(255),
    converted            BOOLEAN,
    converted_at         TIMESTAMP WITHOUT TIME ZONE,
    converted_by         UUID,
    converted_contact_id UUID,
    created_by           UUID,
    updated_by           UUID,
    created_at           TIMESTAMP WITHOUT TIME ZONE,
    updated_at           TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_lead PRIMARY KEY (id)
);

ALTER TABLE lead
    ADD CONSTRAINT FK_LEAD_ON_CONVERTED_BY FOREIGN KEY (converted_by) REFERENCES users (id);

ALTER TABLE lead
    ADD CONSTRAINT FK_LEAD_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES users (id);

ALTER TABLE lead
    ADD CONSTRAINT FK_LEAD_ON_ORGANIZATION FOREIGN KEY (organization_id) REFERENCES organization (id);

ALTER TABLE lead
    ADD CONSTRAINT FK_LEAD_ON_OWNER FOREIGN KEY (owner_id) REFERENCES users (id);

ALTER TABLE lead
    ADD CONSTRAINT FK_LEAD_ON_UPDATED_BY FOREIGN KEY (updated_by) REFERENCES users (id);