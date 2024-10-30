package lt.liutikas.bananacar_notification_svc.application;

import lombok.RequiredArgsConstructor;
import lt.liutikas.bananacar_notification_svc.application.port.in.FetchLatestRidesPort;
import lt.liutikas.bananacar_notification_svc.application.port.in.FetchRidesByBananacarRideIdPort;
import lt.liutikas.bananacar_notification_svc.application.port.out.NotifyRideSubscriptionsPort;
import lt.liutikas.bananacar_notification_svc.application.port.out.SaveRidesPort;
import lt.liutikas.bananacar_notification_svc.common.Loggable;
import lt.liutikas.bananacar_notification_svc.common.RidesScanProperties;
import lt.liutikas.bananacar_notification_svc.domain.Ride;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ScanRidesUseCase implements Loggable {

    private final FetchLatestRidesPort fetchLatestRidesPort;
    private final FetchRidesByBananacarRideIdPort fetchRidesByBananacarRideIdPort;
    private final SaveRidesPort saveRidesPort;
    private final NotifyRideSubscriptionsPort notifyRideSubscriptionsPort;
    private final RidesScanProperties ridesScanProperties;

    public void scan() {

        getLogger().info("Starting BananaCar ride scan");

        List<Ride> newRides = fetchNewRides();

        getLogger().info("Scanned {} new rides", newRides.size());

        saveRidesPort.save(newRides);
        notifyRideSubscriptionsPort.notifySubscriptions(newRides);

        getLogger().info("Completed BananaCar ride scan");
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
