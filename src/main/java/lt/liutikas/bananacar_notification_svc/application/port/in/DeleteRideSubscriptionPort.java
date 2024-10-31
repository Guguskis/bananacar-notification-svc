package lt.liutikas.bananacar_notification_svc.application.port.in;

import lt.liutikas.bananacar_notification_svc.domain.RideSubscription;

import java.util.Optional;

public interface DeleteRideSubscriptionPort {

    Optional<RideSubscription> delete(int id);
}
