-- liquibase formatted sql

-- changeset gordey_dovydenko:3
CREATE
    EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE Client
(
    client_id UUID PRIMARY KEY,
    name      VARCHAR(60) NOT NULL,
    email     VARCHAR(60) NOT NULL,
    password VARCHAR(300) NOT NULL,
    birth_date timestamp with time zone,
    gender VARCHAR(30) NOT NULL CHECK ( gender IN ('MALE', 'FEMALE', 'UNSPECIFIED') ),
    created_date timestamp with time zone NOT NULL
);
-- rollback DROP TABLE Client;

