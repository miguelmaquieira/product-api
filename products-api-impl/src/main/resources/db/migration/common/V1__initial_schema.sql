CREATE TABLE rates
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    brand_id        INT            NOT NULL,
    start_date      TIMESTAMP      NOT NULL,
    end_date        TIMESTAMP      NOT NULL,
    price_list_id   INT            NOT NULL,
    product_id      BIGINT         NOT NULL,
    priority        SMALLINT,
    price           DECIMAL(10, 4) NOT NULL,
    currency        VARCHAR(3)     NOT NULL,
    CONSTRAINT unique_rate UNIQUE (brand_id, start_date, end_date, product_id, currency)
);

-- Create a compound index on product_id, price_list_id, currency, start_date, and end_date
CREATE INDEX idx_rate_product_price_currency_dates
    ON rates (brand_id, product_id, currency, start_date, end_date);

ALTER TABLE rates
    ADD CONSTRAINT chk_dates CHECK (start_date < end_date);

