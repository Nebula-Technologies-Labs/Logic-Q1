CREATE TABLE organization_invites (
                                      id UUID PRIMARY KEY,

                                      token VARCHAR(255) NOT NULL,
                                      email VARCHAR(255) NOT NULL,

                                      role_id UUID NOT NULL,
                                      organization_id UUID NOT NULL,
                                      invited_by UUID NOT NULL,

                                      status VARCHAR(32) NOT NULL DEFAULT 'PENDING',

                                      expires_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                                      created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                      CONSTRAINT uk_organization_invites_token
                                          UNIQUE (token),

                                      CONSTRAINT ck_organization_invites_status
                                          CHECK (status IN ('PENDING', 'ACCEPTED', 'EXPIRED', 'REVOKED')),

                                      CONSTRAINT fk_organization_invites_role
                                          FOREIGN KEY (role_id)
                                              REFERENCES roles(id)
                                              ON DELETE CASCADE,

                                      CONSTRAINT fk_organization_invites_organization
                                          FOREIGN KEY (organization_id)
                                              REFERENCES organization(id)
                                              ON DELETE CASCADE,

                                      CONSTRAINT fk_organization_invites_invited_by
                                          FOREIGN KEY (invited_by)
                                              REFERENCES users(id)
                                              ON DELETE CASCADE
);