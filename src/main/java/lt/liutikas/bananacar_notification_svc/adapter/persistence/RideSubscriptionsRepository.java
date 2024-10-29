package lt.liutikas.bananacar_notification_svc.adapter.persistence;

import lt.liutikas.bananacar_notification_svc.application.port.in.FetchRideSubscriptionsPort;
import lt.liutikas.bananacar_notification_svc.domain.RideSubscription;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public class RideSubscriptionsRepository implements FetchRideSubscriptionsPort {

    @Override
    public List<RideSubscription> fetch() {

        LocalDateTime now = LocalDateTime.now();

        return List.of(
                RideSubscription.builder()
                        .subscriptionId(UUID.randomUUID())
                        .departsOnEarliest(now)
                        .departsOnLatest(now.plusDays(1))
                        .originCity("Vilnius")
                        .destinationCity("Klaipėda")
                        .createdOn(now)
                        .updatedOn(now)
                        .build()
        );
    }
}
