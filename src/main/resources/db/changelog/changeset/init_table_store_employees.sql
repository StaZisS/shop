-- liquibase formatted sql

-- changeset gordey_dovydenko:1
CREATE
    EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE Store_employees
(
    store_id  UUID,
    client_id UUID,
    role      VARCHAR(30) NOT NULL CHECK ( role IN ('ADMIN', 'DEFAULT') ),
    FOREIGN KEY (store_id) REFERENCES Store (store_id),
    FOREIGN KEY (client_id) REFERENCES Client (client_id)
);
-- rollback DROP TABLE Store_employees;