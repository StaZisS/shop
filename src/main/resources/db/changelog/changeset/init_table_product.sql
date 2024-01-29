-- liquibase formatted sql

-- changeset gordey_dovydenko:1
CREATE
    EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE Product
(
    code            VARCHAR(60) PRIMARY KEY,
    store_id        UUID         NOT NULL,
    name            VARCHAR(120) NOT NULL,
    normalized_name VARCHAR(120) NOT NULL,
    price           DECIMAL      NOT NULL,
    rating          DOUBLE PRECISION,
    order_quantity  INTEGER,
    additional_info JSONB,
    FOREIGN KEY (store_id) REFERENCES Store (store_id)
);
-- rollback DROP TABLE Product;

-- changeset gordey_dovydenko:2
CREATE OR REPLACE FUNCTION get_products_paged(
    page_size INTEGER,
    page_number INTEGER,
    query VARCHAR(120) DEFAULT NULL,
    sort_option VARCHAR DEFAULT NULL
)
    RETURNS TABLE
            (
                code            VARCHAR(60),
                name            VARCHAR(120),
                normalized_name VARCHAR(120),
                price           DECIMAL,
                rating          DOUBLE PRECISION,
                order_quantity  INTEGER,
                additional_info JSONB,
                count_page      INTEGER
            )
AS
'
    DECLARE
        offset_value INTEGER;
        total_pages  INTEGER;
    BEGIN
        offset_value := (page_number - 1) * page_size;

        SELECT CEIL(COUNT(*)::NUMERIC / page_size)
        INTO total_pages
        FROM Product
        WHERE (query IS NULL OR Product.normalized_name ILIKE ''%'' || query || ''%'');

        CASE
            WHEN sort_option = ''TOTAL_ORDER_DESC'' THEN RETURN QUERY
                SELECT *,
                       total_pages
                FROM Product
                WHERE (query IS NULL OR Product.normalized_name ILIKE ''%'' || query || ''%'')
                ORDER BY order_quantity DESC
                LIMIT page_size OFFSET offset_value;

            WHEN sort_option = ''PRICE_ASC'' THEN RETURN QUERY
                SELECT *,
                       total_pages
                FROM Product
                WHERE (query IS NULL OR Product.normalized_name ILIKE ''%'' || query || ''%'')
                ORDER BY price
                LIMIT page_size OFFSET offset_value;

            WHEN sort_option = ''PRICE_DESC'' THEN RETURN QUERY
                SELECT *,
                       total_pages
                FROM Product
                WHERE (query IS NULL OR Product.normalized_name ILIKE ''%'' || query || ''%'')
                ORDER BY price DESC
                LIMIT page_size OFFSET offset_value;

            ELSE RETURN QUERY
                SELECT *,
                       total_pages
                FROM Product
                WHERE (query IS NULL OR Product.normalized_name ILIKE ''%'' || query || ''%'')
                ORDER BY normalized_name
                LIMIT page_size OFFSET offset_value;
            END CASE;
    END;
' LANGUAGE plpgsql;

-- changeset gordey_dovydenko:3

INSERT INTO Product (code, name, normalized_name, price, rating, order_quantity, additional_info)
VALUES ('P001', 'Product 1', 'product_1', 29.99, 4.5, 20, '{
  "color": "blue",
  "size": "medium"
}'),
       ('P002', 'Product 2', 'product_2', 39.99, 3.8, 15, '{
         "color": "red",
         "size": "large"
       }'),
       ('P003', 'Product 3', 'product_3', 49.99, 4.2, 25, '{
         "color": "green",
         "size": "small"
       }'),
       ('P004', 'Product 4', 'product_4', 19.99, 4.0, 30, '{
         "color": "black",
         "size": "extra-large"
       }'),
       ('P005', 'Product 5', 'product_5', 59.99, 4.8, 10, '{
         "color": "white",
         "size": "small"
       }');


