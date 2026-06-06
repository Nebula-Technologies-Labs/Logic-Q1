-- Convert existing ordinal values to string values

ALTER TABLE roles
    ALTER COLUMN role TYPE VARCHAR(50)
        USING (
        CASE role
            WHEN 0 THEN 'SUPER_ADMIN'
            WHEN 1 THEN 'ORG_ADMIN'
            WHEN 2 THEN 'MEMBER'
            ELSE 'UNKNOWN'
            END
        );

-- Ensure column remains non-null
ALTER TABLE roles
    ALTER COLUMN role SET NOT NULL;