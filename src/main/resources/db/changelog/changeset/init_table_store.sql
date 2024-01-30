-- liquibase formatted sql

-- changeset gordey_dovydenko:1
CREATE
    EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE Store
(
    store_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name     VARCHAR(60) NOT NULL UNIQUE
);
-- rollback DROP TABLE Store;

