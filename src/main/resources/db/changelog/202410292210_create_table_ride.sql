-- liquibase formatted sql

-- changeset admin:202410292210_create_table_ride runInTransaction:false

CREATE TABLE IF NOT EXISTS ride
(
    id                UUID PRIMARY KEY,
    bananacar_ride_id VARCHAR(50)  NOT NULL,
    locations         JSONB        NOT NULL,
    bananacar_url     VARCHAR(255) NOT NULL,
    departs_on        TIMESTAMP(3) NOT NULL,
    created_on        TIMESTAMP(3) NOT NULL,
    updated_on        TIMESTAMP(3) NOT NULL
);

CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_ride_bananacar_ride_id ON ride (bananacar_ride_id);