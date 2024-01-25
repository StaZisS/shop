-- liquibase formatted sql

-- changeset gordey_dovydenko:3
CREATE
    EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE Client
(
    client_id UUID PRIMARY KEY,
    name      VARCHAR(60) NOT NULL,
    email     VARCHAR(60) NOT NULL
);
-- rollback DROP TABLE Client;
