-- liquibase formatted sql

-- changeset gordey_dovydenko:1
CREATE
    EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE Review
(
    review_id     UUID PRIMARY KEY,
    client_id     UUID,
    product_id    VARCHAR(60),
    created_time  timestamp with time zone,
    modified_time timestamp with time zone,
    rating        INTEGER,
    review_body   JSONB,
    FOREIGN KEY (client_id) REFERENCES Client (client_id),
    FOREIGN KEY (product_id) REFERENCES Product (code)
);
-- rollback DROP TABLE Review;
