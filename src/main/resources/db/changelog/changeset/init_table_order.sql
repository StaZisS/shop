-- liquibase formatted sql

-- changeset gordey_dovydenko:1
CREATE
    EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE Orders
(
    order_id      UUID PRIMARY KEY,
    client_id     UUID        NOT NULL,
    total_price   DECIMAL     NOT NULL,
    status        VARCHAR(30) NOT NULL CHECK ( status IN ('PROCESSING', 'APPLY', 'IN_DELIVER', 'DELIVERED') ),
    creation_date timestamp with time zone,
    track_number  VARCHAR(120),
    address       VARCHAR(120),
    FOREIGN KEY (client_id) REFERENCES Client (client_id)
);

-- rollback DROP TABLE Store_employees;

-- changeset gordey_dovydenko:2
CREATE TABLE Product_in_order
(
    order_id      UUID        NOT NULL,
    product_code  VARCHAR(60) NOT NULL,
    count_product INTEGER     NOT NULL,
    FOREIGN KEY (order_id) REFERENCES Orders (order_id),
    FOREIGN KEY (product_code) REFERENCES Product (code)
);

-- rollback DROP TABLE Product_in_order;