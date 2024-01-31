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
                store_id        UUID,
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

