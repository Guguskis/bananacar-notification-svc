package lt.liutikas.bananacar_notification_svc.application;

import lombok.RequiredArgsConstructor;
import lt.liutikas.bananacar_notification_svc.application.port.in.FetchRideSubscriptionsPort;
import lt.liutikas.bananacar_notification_svc.application.port.out.NotificationPort;
import lt.liutikas.bananacar_notification_svc.application.port.out.NotifyRideSubscriptionsPort;
import lt.liutikas.bananacar_notification_svc.common.Loggable;
import lt.liutikas.bananacar_notification_svc.domain.Ride;
import lt.liutikas.bananacar_notification_svc.domain.RideSubscription;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
public class NotifySubscriptionsUseCase implements Loggable, NotifyRideSubscriptionsPort {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final FetchRideSubscriptionsPort fetchRideSubscriptionsPort;
    private final NotificationPort notificationPort;

    @Override
    public void notifySubscriptions(List<Ride> rides) {

        fetchRideSubscriptionsPort
                .fetch()
                .forEach(subscription -> notify(subscription, rides));
    }

    private void notify(RideSubscription subscription, List<Ride> rides) {

        rides.stream()
                .filter(ride -> isInterested(subscription, ride))
                .forEach(this::notify);
    }

    private boolean isInterested(RideSubscription subscription, Ride ride) {

        return isRouteMatch(ride, subscription) && withinDepartureTimeframe(ride, subscription);
    }

    private boolean isRouteMatch(Ride ride, RideSubscription subscription) {

        return ride.isRouteMatch(
                subscription.getOriginCity(),
                subscription.getDestinationCity()
        );
    }

    private boolean withinDepartureTimeframe(Ride ride, RideSubscription subscription) {

        LocalDateTime subscriptionDepartsOnEarliest = subscription.getDepartsOnEarliest();
        LocalDateTime subscriptionDepartsOnLatest = subscription.getDepartsOnLatest();
        LocalDateTime rideDepartsOn = ride.getDepartsOn();

        boolean rideNotTooEarly = subscriptionDepartsOnEarliest.isBefore(rideDepartsOn) || subscriptionDepartsOnEarliest.equals(rideDepartsOn);
        boolean rideNotTooLate = subscriptionDepartsOnLatest.isAfter(rideDepartsOn) || subscriptionDepartsOnLatest.isEqual(rideDepartsOn);

        return rideNotTooEarly && rideNotTooLate;
    }

    private void notify(Ride ride) {

        String message = "A new ride has appeared from %s to %s with departure on %s"
                .formatted(
                        ride.getOrigin().getCity(),
                        ride.getDestination().getCity(),
                        ride.getDepartsOn().format(DATE_TIME_FORMATTER)
                );

        getLogger().info("Sending ride notification [{}]", message);

        notificationPort.notify(message, ride.getBananacarUrl());
    }
}
