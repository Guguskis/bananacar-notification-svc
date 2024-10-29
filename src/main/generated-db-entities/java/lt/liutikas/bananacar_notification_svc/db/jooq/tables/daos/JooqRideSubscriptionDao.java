/*
 * This file is generated by jOOQ.
 */
package lt.liutikas.bananacar_notification_svc.db.jooq.tables.daos;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lt.liutikas.bananacar_notification_svc.db.jooq.tables.RideSubscription;
import lt.liutikas.bananacar_notification_svc.db.jooq.tables.pojos.JooqRideSubscription;
import lt.liutikas.bananacar_notification_svc.db.jooq.tables.records.JooqRideSubscriptionRecord;

import org.jooq.Configuration;
import org.jooq.impl.DAOImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
@Repository
public class JooqRideSubscriptionDao extends DAOImpl<JooqRideSubscriptionRecord, JooqRideSubscription, UUID> {

    /**
     * Create a new JooqRideSubscriptionDao without any configuration
     */
    public JooqRideSubscriptionDao() {
        super(RideSubscription.RIDE_SUBSCRIPTION, JooqRideSubscription.class);
    }

    /**
     * Create a new JooqRideSubscriptionDao with an attached configuration
     */
    @Autowired
    public JooqRideSubscriptionDao(Configuration configuration) {
        super(RideSubscription.RIDE_SUBSCRIPTION, JooqRideSubscription.class, configuration);
    }

    @Override
    public UUID getId(JooqRideSubscription object) {
        return object.getSubscriptionId();
    }

    /**
     * Fetch records that have <code>subscription_id BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<JooqRideSubscription> fetchRangeOfSubscriptionId(UUID lowerInclusive, UUID upperInclusive) {
        return fetchRange(RideSubscription.RIDE_SUBSCRIPTION.SUBSCRIPTION_ID, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>subscription_id IN (values)</code>
     */
    public List<JooqRideSubscription> fetchBySubscriptionId(UUID... values) {
        return fetch(RideSubscription.RIDE_SUBSCRIPTION.SUBSCRIPTION_ID, values);
    }

    /**
     * Fetch a unique record that has <code>subscription_id = value</code>
     */
    public JooqRideSubscription fetchOneBySubscriptionId(UUID value) {
        return fetchOne(RideSubscription.RIDE_SUBSCRIPTION.SUBSCRIPTION_ID, value);
    }

    /**
     * Fetch a unique record that has <code>subscription_id = value</code>
     */
    public Optional<JooqRideSubscription> fetchOptionalBySubscriptionId(UUID value) {
        return fetchOptional(RideSubscription.RIDE_SUBSCRIPTION.SUBSCRIPTION_ID, value);
    }

    /**
     * Fetch records that have <code>origin_city BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<JooqRideSubscription> fetchRangeOfOriginCity(String lowerInclusive, String upperInclusive) {
        return fetchRange(RideSubscription.RIDE_SUBSCRIPTION.ORIGIN_CITY, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>origin_city IN (values)</code>
     */
    public List<JooqRideSubscription> fetchByOriginCity(String... values) {
        return fetch(RideSubscription.RIDE_SUBSCRIPTION.ORIGIN_CITY, values);
    }

    /**
     * Fetch records that have <code>destination_city BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<JooqRideSubscription> fetchRangeOfDestinationCity(String lowerInclusive, String upperInclusive) {
        return fetchRange(RideSubscription.RIDE_SUBSCRIPTION.DESTINATION_CITY, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>destination_city IN (values)</code>
     */
    public List<JooqRideSubscription> fetchByDestinationCity(String... values) {
        return fetch(RideSubscription.RIDE_SUBSCRIPTION.DESTINATION_CITY, values);
    }

    /**
     * Fetch records that have <code>departs_on_earliest BETWEEN lowerInclusive
     * AND upperInclusive</code>
     */
    public List<JooqRideSubscription> fetchRangeOfDepartsOnEarliest(LocalDateTime lowerInclusive, LocalDateTime upperInclusive) {
        return fetchRange(RideSubscription.RIDE_SUBSCRIPTION.DEPARTS_ON_EARLIEST, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>departs_on_earliest IN (values)</code>
     */
    public List<JooqRideSubscription> fetchByDepartsOnEarliest(LocalDateTime... values) {
        return fetch(RideSubscription.RIDE_SUBSCRIPTION.DEPARTS_ON_EARLIEST, values);
    }

    /**
     * Fetch records that have <code>departs_on_latest BETWEEN lowerInclusive
     * AND upperInclusive</code>
     */
    public List<JooqRideSubscription> fetchRangeOfDepartsOnLatest(LocalDateTime lowerInclusive, LocalDateTime upperInclusive) {
        return fetchRange(RideSubscription.RIDE_SUBSCRIPTION.DEPARTS_ON_LATEST, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>departs_on_latest IN (values)</code>
     */
    public List<JooqRideSubscription> fetchByDepartsOnLatest(LocalDateTime... values) {
        return fetch(RideSubscription.RIDE_SUBSCRIPTION.DEPARTS_ON_LATEST, values);
    }

    /**
     * Fetch records that have <code>created_on BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<JooqRideSubscription> fetchRangeOfCreatedOn(LocalDateTime lowerInclusive, LocalDateTime upperInclusive) {
        return fetchRange(RideSubscription.RIDE_SUBSCRIPTION.CREATED_ON, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>created_on IN (values)</code>
     */
    public List<JooqRideSubscription> fetchByCreatedOn(LocalDateTime... values) {
        return fetch(RideSubscription.RIDE_SUBSCRIPTION.CREATED_ON, values);
    }

    /**
     * Fetch records that have <code>updated_on BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<JooqRideSubscription> fetchRangeOfUpdatedOn(LocalDateTime lowerInclusive, LocalDateTime upperInclusive) {
        return fetchRange(RideSubscription.RIDE_SUBSCRIPTION.UPDATED_ON, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>updated_on IN (values)</code>
     */
    public List<JooqRideSubscription> fetchByUpdatedOn(LocalDateTime... values) {
        return fetch(RideSubscription.RIDE_SUBSCRIPTION.UPDATED_ON, values);
    }
}