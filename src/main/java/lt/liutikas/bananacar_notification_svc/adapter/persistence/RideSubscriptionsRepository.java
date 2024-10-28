package lt.liutikas.bananacar_notification_svc.adapter.persistence;

import lt.liutikas.bananacar_notification_svc.application.port.in.FetchRideSubscriptionsPort;
import lt.liutikas.bananacar_notification_svc.domain.RideSubscription;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RideSubscriptionsRepository implements FetchRideSubscriptionsPort {

    @Override
    public List<RideSubscription> fetch() {

        // todo
        return null;
    }
}
