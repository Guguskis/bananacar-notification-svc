package lt.liutikas.bananacar_notification_svc.application;

import lombok.RequiredArgsConstructor;
import lt.liutikas.bananacar_notification_svc.application.port.in.FetchLatestRidesPort;
import lt.liutikas.bananacar_notification_svc.application.port.in.FetchRidesByRideIdPort;
import lt.liutikas.bananacar_notification_svc.application.port.out.SaveRidesPort;
import lt.liutikas.bananacar_notification_svc.common.Loggable;
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
    private final FetchRidesByRideIdPort fetchRidesByRideIdPort;
    private final SaveRidesPort saveRidesPort;
    private final NotifySubscriptionsUseCase notifySubscriptionsUseCase;

    public void scan() {

        getLogger().info("Starting BananaCar ride scan");

        List<Ride> newRides = fetchNewRides();

        getLogger().info("Scanned {} new rides", newRides.size());

        saveRidesPort.save(newRides);
        notifySubscriptionsUseCase.notifySubscriptions(newRides);

        getLogger().info("Completed BananaCar ride scan");
    }

    private List<Ride> fetchNewRides() {

        LocalDateTime maximumDepartsOn = LocalDateTime.now().plusDays(2);
        List<Ride> latestRides = fetchLatestRidesPort.fetch(maximumDepartsOn);

        return filterNewRides(latestRides);
    }

    private List<Ride> filterNewRides(List<Ride> rides) {

        List<String> rideIds = rides.stream()
                .map(Ride::getRideId)
                .toList();

        Set<String> existingRideIds = fetchRidesByRideIdPort.fetch(rideIds)
                .stream()
                .map(Ride::getRideId)
                .collect(Collectors.toSet());

        return rides.stream()
                .filter(ride -> !existingRideIds.contains(ride.getRideId()))
                .toList();
    }
}
