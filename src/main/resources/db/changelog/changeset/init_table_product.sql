-- liquibase formatted sql

-- changeset gordey_dovydenko:1
CREATE
EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE Product
(
    code            VARCHAR(60) PRIMARY KEY,
    name            VARCHAR(120) NOT NULL,
    normalized_name VARCHAR(120) NOT NULL,
    price           DECIMAL      NOT NULL,
    rating          DOUBLE PRECISION,
    order_quantity  INTEGER,
    additional_info JSONB
);
-- rollback DROP TABLE Product;