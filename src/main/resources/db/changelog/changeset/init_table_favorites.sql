-- liquibase formatted sql

-- changeset gordey_dovydenko:1
CREATE
    EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE Favorites
(
    client_id    UUID        NOT NULL,
    product_code VARCHAR(60) NOT NULL,
    FOREIGN KEY (client_id) REFERENCES Client (code),
    FOREIGN KEY (product_code) REFERENCES Product (product_id)
);

-- rollback DROP TABLE Favorites;