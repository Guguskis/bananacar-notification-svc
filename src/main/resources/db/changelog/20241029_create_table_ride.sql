-- liquibase formatted sql

-- changeset admin:20241029_create_table_ride
CREATE TABLE IF NOT EXISTS ride
(
    ride_id             INTEGER PRIMARY KEY,
    origin_city         VARCHAR(100) NOT NULL,
    destination_city    VARCHAR(100) NOT NULL,
    departs_on_earliest TIMESTAMP,
    departs_on_latest   TIMESTAMP
);
