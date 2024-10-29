-- liquibase formatted sql

-- changeset admin:20241029_create_table_ride_subscription

CREATE TABLE IF NOT EXISTS ride_subscription
(
    id
    UUID
    PRIMARY
    KEY,
    origin_city
    VARCHAR
(
    100
) NOT NULL,
    destination_city VARCHAR
(
    100
) NOT NULL,
    departs_on_earliest TIMESTAMP,
    departs_on_latest TIMESTAMP
    );