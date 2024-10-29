package lt.liutikas.bananacar_notification_svc.application;

import lombok.RequiredArgsConstructor;
import lt.liutikas.bananacar_notification_svc.application.port.in.FetchRideSubscriptionsPort;
import lt.liutikas.bananacar_notification_svc.application.port.out.NotificationPort;
import lt.liutikas.bananacar_notification_svc.domain.Ride;
import lt.liutikas.bananacar_notification_svc.domain.RideSubscription;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class NotifySubscriptionsUseCase {

    private final FetchRideSubscriptionsPort fetchRideSubscriptionsPort;
    private final NotificationPort notificationPort;

    public void notifySubscriptions(List<Ride> rides) {

        fetchRideSubscriptionsPort
                .fetch()
                .forEach(subscription -> notify(subscription, rides));
    }

    private void notify(RideSubscription subscription, List<Ride> rides) {

        rides.stream()
                .filter(ride -> isInterested(subscription, ride))
                .forEach(ride -> notify(subscription, ride));
    }

    private boolean isInterested(RideSubscription subscription, Ride ride) {

        return isRouteMatch(ride, subscription) && withinDepartureTimeframe(ride, subscription);
    }

    private boolean isRouteMatch(Ride ride, RideSubscription subscription) {

        return ride.getRoute().isRouteMatch(
                subscription.getOriginCity(),
                subscription.getDestinationCity()
        );
    }

    private boolean withinDepartureTimeframe(Ride ride, RideSubscription subscription) {

        LocalDateTime subscriptionDepartsOnEarliest = subscription.getDepartsOnEarliest();
        LocalDateTime subscriptionDepartsOnLatest = subscription.getDepartsOnLatest();
        LocalDateTime rideDepartsOn = ride.getDepartsOn();

        boolean rideNotTooEarly = subscriptionDepartsOnEarliest.isAfter(rideDepartsOn) || subscriptionDepartsOnEarliest.equals(rideDepartsOn);
        boolean rideNotTooLate = subscriptionDepartsOnLatest.isAfter(rideDepartsOn) || subscriptionDepartsOnLatest.isEqual(rideDepartsOn);

        return rideNotTooEarly && rideNotTooLate;
    }

    private void notify(RideSubscription subscription, Ride ride) {

        String message = "A new ride from %s to %s has appeared"
                .formatted(
                        ride.getRoute().getOrigin().getCity(),
                        ride.getRoute().getDestination().getCity()
                );

        notificationPort.notifyUser(subscription.getUserId(), message, ride.getBananacarUrl());
    }
}
