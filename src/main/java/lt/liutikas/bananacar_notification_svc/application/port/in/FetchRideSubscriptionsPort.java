package lt.liutikas.bananacar_notification_svc.application.port.in;

import lt.liutikas.bananacar_notification_svc.domain.RideSubscription;

import java.util.List;

public interface FetchRideSubscriptionsPort {

    List<RideSubscription> fetch();
}
