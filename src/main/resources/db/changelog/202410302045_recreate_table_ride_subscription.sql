-- liquibase formatted sql logicalFilePath:202410302045_recreate_table_ride_subscription.sql

-- changeset admin:202410302045_recreate_table_ride_subscription

DROP TABLE ride_subscription;

CREATE TABLE IF NOT EXISTS ride_subscription
(
    id                  INT PRIMARY KEY,
    origin_city         VARCHAR(100) NOT NULL,
    destination_city    VARCHAR(100) NOT NULL,
    departs_on_earliest TIMESTAMP(3) NOT NULL,
    departs_on_latest   TIMESTAMP(3) NOT NULL,
    created_on          TIMESTAMP(3) NOT NULL,
    updated_on          TIMESTAMP(3) NOT NULL
);
