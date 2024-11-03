package lt.liutikas.bananacar_notification_svc.application;

import lombok.RequiredArgsConstructor;
import lt.liutikas.bananacar_notification_svc.application.port.in.FetchRideSubscriptionsPort;
import lt.liutikas.bananacar_notification_svc.application.port.out.NotifyNewRidePort;
import lt.liutikas.bananacar_notification_svc.application.port.out.NotifySubscriptionsPort;
import lt.liutikas.bananacar_notification_svc.common.Loggable;
import lt.liutikas.bananacar_notification_svc.domain.Ride;
import lt.liutikas.bananacar_notification_svc.domain.RideSubscription;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class NotifySubscriptionsUseCase implements Loggable, NotifySubscriptionsPort {

    private final FetchRideSubscriptionsPort fetchRideSubscriptionsPort;
    private final NotifyNewRidePort notifyNewRidePort;

    @Override
    public void notify(List<Ride> rides) {

        fetchRideSubscriptionsPort
                .fetch()
                .forEach(subscription -> notifyIfInterested(subscription, rides));
    }

    private void notifyIfInterested(RideSubscription subscription, List<Ride> rides) {

        rides.stream()
                .filter(ride -> isInterested(subscription, ride))
                .forEach(notifyNewRidePort::notify);
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

}
