package lt.liutikas.bananacar_notification_svc.adapter.web.bananacar;

import lombok.RequiredArgsConstructor;
import lt.liutikas.bananacar_notification_svc.adapter.web.bananacar.model.BananacarPage;
import lt.liutikas.bananacar_notification_svc.application.port.in.FetchLatestRidesPort;
import lt.liutikas.bananacar_notification_svc.common.util.Loggable;
import lt.liutikas.bananacar_notification_svc.domain.Ride;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BananacarService implements Loggable, FetchLatestRidesPort {

    private final BananacarPage bananacarPage;

    @Override
    public List<Ride> fetch(LocalDateTime maximumDepartsOn) {

        List<Ride> rides = new ArrayList<>();

        LocalDateTime farthestDepartsOn = null;

        do {

            if (farthestDepartsOn == null) {
                bananacarPage.navigateToRidesHomePage();
            } else {
                bananacarPage.navigateToRidesNextPage();
            }

            List<Ride> pageRides = bananacarPage.getRides();

            if (pageRides.isEmpty()) {
                break;
            }

            rides.addAll(pageRides);

            farthestDepartsOn = rides.stream()
                    .map(Ride::getDepartsOn)
                    .max(LocalDateTime::compareTo)
                    .orElse(maximumDepartsOn);

        } while (farthestDepartsOn.isBefore(maximumDepartsOn) && !bananacarPage.isLastPage());

        return removeDuplicates(rides);
    }

    private List<Ride> removeDuplicates(List<Ride> rides) {

        return rides.stream()
                .collect(Collectors.toMap(
                        Ride::getBananacarRideId,
                        Function.identity(),
                        (existing, replacement) -> replacement
                ))
                .values()
                .stream()
                .toList();
    }
}
