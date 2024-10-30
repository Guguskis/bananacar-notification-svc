package lt.liutikas.bananacar_notification_svc.adapter.persistence;

import lombok.RequiredArgsConstructor;
import lt.liutikas.bananacar_notification_svc.application.CreateRideSubscriptionPort;
import lt.liutikas.bananacar_notification_svc.application.port.in.FetchRideSubscriptionsPort;
import lt.liutikas.bananacar_notification_svc.db.jooq.tables.records.JooqRideSubscriptionRecord;
import lt.liutikas.bananacar_notification_svc.domain.RideSubscription;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

import static lt.liutikas.bananacar_notification_svc.db.jooq.Tables.RIDE_SUBSCRIPTION;

@Repository
@RequiredArgsConstructor
public class RideSubscriptionsRepository implements FetchRideSubscriptionsPort, CreateRideSubscriptionPort {

    private final DSLContext dslContext;

    @Override
    public List<RideSubscription> fetch() {

        return dslContext.selectFrom(RIDE_SUBSCRIPTION)
                .fetchInto(JooqRideSubscriptionRecord.class)
                .stream()
                .map(RideSubscriptionsRepository::toDomain)
                .toList();
    }

    @Override
    public RideSubscription create(RideSubscription rideSubscription) {

        JooqRideSubscriptionRecord record = toJooq(rideSubscription);

        JooqRideSubscriptionRecord persistedRecord = dslContext.insertInto(RIDE_SUBSCRIPTION)
                .set(record)
                .returning()
                .fetchSingle();

        return toDomain(persistedRecord);
    }

    private static JooqRideSubscriptionRecord toJooq(RideSubscription rideSubscription) {

        JooqRideSubscriptionRecord record = new JooqRideSubscriptionRecord();

        record.setOriginCity(rideSubscription.getOriginCity());
        record.setDestinationCity(rideSubscription.getDestinationCity());
        record.setDepartsOnEarliest(rideSubscription.getDepartsOnEarliest());
        record.setDepartsOnLatest(rideSubscription.getDepartsOnLatest());
        record.setCreatedOn(rideSubscription.getCreatedOn());
        record.setUpdatedOn(rideSubscription.getUpdatedOn());

        return record;
    }

    private static RideSubscription toDomain(JooqRideSubscriptionRecord record) {

        return RideSubscription.builder()
                .id(record.getId())
                .originCity(record.getOriginCity())
                .destinationCity(record.getDestinationCity())
                .departsOnEarliest(record.getDepartsOnEarliest())
                .departsOnLatest(record.getDepartsOnLatest())
                .createdOn(record.getCreatedOn())
                .updatedOn(record.getUpdatedOn())
                .build();
    }
}
