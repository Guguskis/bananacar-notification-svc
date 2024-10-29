package lt.liutikas.bananacar_notification_svc.adapter.web;

import lombok.RequiredArgsConstructor;
import lt.liutikas.bananacar_notification_svc.application.port.in.FetchLatestRidesPort;
import lt.liutikas.bananacar_notification_svc.common.Loggable;
import lt.liutikas.bananacar_notification_svc.domain.Ride;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BananacarService implements Loggable, FetchLatestRidesPort {

    private final BananacarPage bananacarPage;

    @Override
    public List<Ride> fetch(LocalDateTime maximumDepartsOn) {

        List<Ride> rides = new ArrayList<>();

        LocalDateTime farthestDepartsOn = null;
        int previousPage = -1;

        do {
            int currentPage;

            if (farthestDepartsOn == null) {
                currentPage = bananacarPage.navigateToRidesHomePage();
            } else {
                currentPage = bananacarPage.navigateToRidesNextPage();
            }

            List<Ride> pageRides = bananacarPage.getRides();

            if (pageRides.isEmpty() || previousPage == currentPage) {
                break;
            }

            rides.addAll(pageRides);

            previousPage = currentPage;
            farthestDepartsOn = rides.stream()
                    .map(Ride::getDepartsOn)
                    .max(LocalDateTime::compareTo)
                    .orElse(maximumDepartsOn);

        } while (farthestDepartsOn.isBefore(maximumDepartsOn));

        return rides;
    }
}
