package lt.liutikas.bananacar_notification_svc.application;

import lombok.RequiredArgsConstructor;
import lt.liutikas.bananacar_notification_svc.application.port.in.CreateRidesPort;
import lt.liutikas.bananacar_notification_svc.application.port.in.FetchLatestRidesPort;
import lt.liutikas.bananacar_notification_svc.application.port.in.FetchRideSubscriptionsPort;
import lt.liutikas.bananacar_notification_svc.application.port.in.FetchRidesByBananacarRideIdPort;
import lt.liutikas.bananacar_notification_svc.application.port.out.NotifySubscriptionsPort;
import lt.liutikas.bananacar_notification_svc.common.properties.RidesScanProperties;
import lt.liutikas.bananacar_notification_svc.common.util.Loggable;
import lt.liutikas.bananacar_notification_svc.domain.Ride;
import lt.liutikas.bananacar_notification_svc.domain.RideSubscription;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ScanRidesUseCase implements Loggable {

    private final FetchLatestRidesPort fetchLatestRidesPort;
    private final FetchRidesByBananacarRideIdPort fetchRidesByBananacarRideIdPort;
    private final CreateRidesPort createRidesPort;
    private final NotifySubscriptionsPort notifySubscriptionsPort;
    private final RidesScanProperties ridesScanProperties;
    private final FetchRideSubscriptionsPort fetchRideSubscriptionsPor;

    @Transactional
    public void scan() {

        getLogger().debug("Starting BananaCar ride scan");

        List<RideSubscription> subscriptions = fetchRideSubscriptionsPor.fetch();

        if (subscriptions.isEmpty()) {
            getLogger().debug("Skipped BananaCar ride scan due to lack of subscriptions");
            return;
        }

        List<Ride> newRides = fetchNewRides();

        getLogger().info("Scanned {} new rides", newRides.size());

        createRidesPort.save(newRides);
        notifySubscriptionsPort.notify(newRides);

        getLogger().debug("Completed BananaCar ride scan");
    }

    private List<Ride> fetchNewRides() {

        LocalDateTime maximumDepartsOn = LocalDateTime.now().plusDays(ridesScanProperties.getMaximumDepartureInDays());
        List<Ride> latestRides = fetchLatestRidesPort.fetch(maximumDepartsOn);

        return filterNewRides(latestRides);
    }

    private List<Ride> filterNewRides(List<Ride> rides) {

        List<String> bananacarRideIds = rides.stream()
                .map(Ride::getBananacarRideId)
                .toList();

        Set<String> existingRideIds = fetchRidesByBananacarRideIdPort.fetch(bananacarRideIds)
                .stream()
                .map(Ride::getBananacarRideId)
                .collect(Collectors.toSet());

        return rides.stream()
                .filter(ride -> !existingRideIds.contains(ride.getBananacarRideId()))
                .toList();
    }
}
