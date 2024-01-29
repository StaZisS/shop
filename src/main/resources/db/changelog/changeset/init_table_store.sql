-- liquibase formatted sql

-- changeset gordey_dovydenko:1
CREATE
    EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE Store
(
    store_id UUID PRIMARY KEY,
    name     VARCHAR(60) NOT NULL
);
-- rollback DROP TABLE Store;

