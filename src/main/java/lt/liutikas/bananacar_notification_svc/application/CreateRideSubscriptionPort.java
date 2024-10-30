package lt.liutikas.bananacar_notification_svc.application;

import lt.liutikas.bananacar_notification_svc.domain.RideSubscription;

public interface CreateRideSubscriptionPort {

    RideSubscription create(RideSubscription rideSubscription);
}
