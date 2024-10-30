package lt.liutikas.bananacar_notification_svc.adapter.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lt.liutikas.bananacar_notification_svc.adapter.web.model.BananacarLocation;
import lt.liutikas.bananacar_notification_svc.adapter.web.model.BananacarRide;
import lt.liutikas.bananacar_notification_svc.adapter.web.model.BananacarRideSearchResponse;
import lt.liutikas.bananacar_notification_svc.common.Loggable;
import lt.liutikas.bananacar_notification_svc.domain.Location;
import lt.liutikas.bananacar_notification_svc.domain.LocationType;
import lt.liutikas.bananacar_notification_svc.domain.Ride;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.core.har.HarEntry;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BananacarPage implements Loggable {

    private static final String BANANACAR_BASE_URL = "https://bananacar.lt/en";
    private static final String RIDE_URL_TEMPLATE = BANANACAR_BASE_URL + "/ride/find?ride_id=%s";
    private static final String RIDES_URL_TEMPLATE = BANANACAR_BASE_URL + "/ride/find?page=%s";

    private final WebDriver webDriver;
    private final BrowserMobProxy proxy;
    private final ObjectMapper objectMapper;

    private int currentPageNumber = 1;

    public int navigateToRidesHomePage() {

        cleanUpExistingNetworkLogs();

        currentPageNumber = 1;
        navigateToPage(currentPageNumber);

        return currentPageNumber;
    }

    public int navigateToRidesNextPage() {

        int maxPageNumber = getMaxPageNumber();

        if (currentPageNumber >= maxPageNumber) {
            getLogger().warn("Cannot navigate to next page, currentPage [{}], maxPage [{}]", currentPageNumber, maxPageNumber);
            return currentPageNumber;
        }

        cleanUpExistingNetworkLogs();

        currentPageNumber++;
        navigateToPage(currentPageNumber);

        return currentPageNumber;
    }

    private int getMaxPageNumber() {

        return proxy.getHar().getLog().getEntries().stream()
                .filter(BananacarPage::isRidesSearchRequest)
                .map(h -> h.getResponse().getContent().getText())
                .map(this::parseBananacarRideSearchResponse)
                .findFirst()
                .map(BananacarRideSearchResponse::getLastPage)
                .orElseThrow(() -> new IllegalArgumentException("RidesSearchRequest not found"));
    }

    public List<Ride> getRides() {

        return proxy.getHar().getLog().getEntries().stream()
                .filter(BananacarPage::isRidesSearchRequest)
                .map(h -> h.getResponse().getContent().getText())
                .map(this::parseBananacarRideSearchResponse)
                .map(BananacarRideSearchResponse::getRides)
                .flatMap(Collection::stream)
                .map(this::toRides)
                .collect(Collectors.toMap(
                        Ride::getBananacarRideId,
                        Function.identity(),
                        (existing, replacement) -> replacement
                ))
                .values()
                .stream()
                .toList();
    }

    private void navigateToPage(int pageNumber) {

        webDriver.navigate().to(RIDES_URL_TEMPLATE.formatted(pageNumber));

        forceWait(Duration.ofMillis(200));
    }

    private Ride toRides(BananacarRide ride) {

        try {

            List<Location> locations = mapRoute(ride.getLocations());
            URL bananacarUrl = new URL(RIDE_URL_TEMPLATE.formatted(ride.getId()));
            LocalDateTime departureDatetime = ride.getDepartureDatetime();

            return Ride.builder()
                    .bananacarRideId(ride.getId())
                    .locations(locations)
                    .departsOn(departureDatetime)
                    .bananacarUrl(bananacarUrl)
                    .createdOn(LocalDateTime.now())
                    .updatedOn(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Error mapping BananacarRide", e);
        }
    }

    private List<Location> mapRoute(List<BananacarLocation> bananacarLocations) {

        List<Location> locations = new ArrayList<>();

        for (int i = 0; i < bananacarLocations.size(); i++) {

            BananacarLocation location = bananacarLocations.get(i);

            locations.add(Location.builder()
                    .city(location.getCity())
                    .type(resolveLocationType(location.getOrd(), bananacarLocations.size()))
                    .build());
        }

        return locations;
    }

    private LocationType resolveLocationType(int currentLocationOrder, int locationsCount) {

        if (currentLocationOrder == 0) {
            return LocationType.ORIGIN;
        } else if (currentLocationOrder == locationsCount - 1) {
            return LocationType.DESTINATION;
        } else {
            return LocationType.INTERMEDIARY;
        }

    }

    private void cleanUpExistingNetworkLogs() {

        proxy.newHar();
    }

    private static boolean isRidesSearchRequest(HarEntry har) {

        return har.getRequest().getUrl().contains("/api/v1/rides/search");
    }

    private BananacarRideSearchResponse parseBananacarRideSearchResponse(String responseBody) {

        try {
            return objectMapper.readValue(responseBody, BananacarRideSearchResponse.class); // fixme sometimes null
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to parse BananacarRideSearchResponse", e);
        }
    }

    private void forceWait(Duration timeout) {

        try {
            Thread.sleep(timeout.toMillis());
        } catch (InterruptedException e) {
            getLogger().error("Thread sleep error", e);
        }
    }
}
