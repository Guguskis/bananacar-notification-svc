package lt.liutikas.bananacar_notification_svc.application;

import lombok.RequiredArgsConstructor;
import lt.liutikas.bananacar_notification_svc.application.port.in.FetchLatestRidesPort;
import lt.liutikas.bananacar_notification_svc.application.port.in.FetchRidesByRideIdPort;
import lt.liutikas.bananacar_notification_svc.application.port.out.SaveRidesPort;
import lt.liutikas.bananacar_notification_svc.domain.Ride;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ScanRidesUseCase {

    private final FetchLatestRidesPort fetchLatestRidesPort;
    private final FetchRidesByRideIdPort fetchRidesByRideIdPort;
    private final SaveRidesPort saveRidesPort;
    private final NotifySubscriptionsUseCase notifySubscriptionsUseCase;

    public void scan() {

        List<Ride> rides = fetchRides();

        saveRidesPort.save(rides);
        notifySubscriptionsUseCase.notifySubscriptions(rides);
    }

    private List<Ride> fetchRides() {

        LocalDateTime maximumDepartsOn = LocalDateTime.now().plusDays(2);
        List<Ride> latestRides = fetchLatestRidesPort.fetch(maximumDepartsOn);

        return filterNewRides(latestRides);
    }

    private List<Ride> filterNewRides(List<Ride> rides) {

        List<String> rideIds = rides.stream()
                .map(Ride::rideId)
                .toList();

        Set<String> existingRideIds = fetchRidesByRideIdPort.fetch(rideIds)
                .stream()
                .map(Ride::rideId)
                .collect(Collectors.toSet());

        return rides.stream()
                .filter(ride -> !existingRideIds.contains(ride.rideId()))
                .toList();
    }
}
