-- Add new column to users table
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS is_terms_accepted BOOLEAN NOT NULL DEFAULT FALSE;

-- Create refresh_tokens table
CREATE TABLE IF NOT EXISTS refresh_tokens (
                                              id UUID PRIMARY KEY,

                                              token VARCHAR(1000) NOT NULL UNIQUE,

                                              user_id UUID NOT NULL,

                                              expires_at TIMESTAMP NOT NULL,

                                              revoked BOOLEAN NOT NULL DEFAULT FALSE,

                                              used BOOLEAN NOT NULL DEFAULT FALSE,

                                              device_name VARCHAR(255),

                                              ip_address VARCHAR(255),

                                              created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                              CONSTRAINT fk_refresh_tokens_user
                                                  FOREIGN KEY (user_id)
                                                      REFERENCES users(id)
                                                      ON DELETE CASCADE
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user_id
    ON refresh_tokens(user_id);

CREATE INDEX IF NOT EXISTS idx_refresh_tokens_token
    ON refresh_tokens(token);

CREATE INDEX IF NOT EXISTS idx_refresh_tokens_expires_at
    ON refresh_tokens(expires_at);

CREATE INDEX IF NOT EXISTS idx_refresh_tokens_revoked
    ON refresh_tokens(revoked);