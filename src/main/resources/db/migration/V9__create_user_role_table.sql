CREATE TABLE user_role (
	id UUID PRIMARY KEY,
	user_id UUID NOT NULL UNIQUE,
	CONSTRAINT fk_user_role_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_user_role_user_id ON user_role (user_id);

