-- liquibase formatted sql

-- changeset gordey_dovydenko:4
CREATE
    EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE Review
(
    review_id     UUID PRIMARY KEY,
    client_id     UUID,
    created_time  timestamp with time zone,
    modified_time timestamp with time zone,
    rating        INTEGER,
    review_body   JSONB,
    FOREIGN KEY (client_id) REFERENCES Client (client_id)
);
-- rollback DROP TABLE Review;
