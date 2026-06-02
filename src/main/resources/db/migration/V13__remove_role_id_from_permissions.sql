-- Vxx__remove_role_id_from_permissions.sql

ALTER TABLE permissions
DROP COLUMN role_id;