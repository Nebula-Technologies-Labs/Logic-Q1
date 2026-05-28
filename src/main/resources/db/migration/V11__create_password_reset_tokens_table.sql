CREATE TABLE password_reset_tokens (
	id UUID PRIMARY KEY,
	user_id UUID NOT NULL UNIQUE,
	token INTEGER,
	expire_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
	created_at TIMESTAMP WITH TIME ZONE NOT NULL,
	verified BOOLEAN NOT NULL DEFAULT FALSE,
	CONSTRAINT fk_password_reset_tokens_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_password_reset_tokens_user_id ON password_reset_tokens (user_id);
CREATE INDEX idx_password_reset_tokens_token ON password_reset_tokens (token);
CREATE INDEX idx_password_reset_tokens_expire_at ON password_reset_tokens (expire_at);
CREATE INDEX idx_password_reset_tokens_verified ON password_reset_tokens (verified);

