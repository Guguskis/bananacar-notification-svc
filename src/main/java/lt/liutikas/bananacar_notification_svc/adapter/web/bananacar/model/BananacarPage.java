package lt.liutikas.bananacar_notification_svc.adapter.web.bananacar.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
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
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class BananacarPage implements Loggable {

    private static final String BANANACAR_BASE_URL = "https://bananacar.lt/en";
    private static final String RIDE_URL_TEMPLATE = BANANACAR_BASE_URL + "/ride/%s";
    private static final String RIDES_URL_TEMPLATE = BANANACAR_BASE_URL + "/ride/find?page=%s";
    private static final String RIDES_SEARCH_API_PATH = "/api/v1/rides/search";

    private final WebDriver webDriver;
    private final BrowserMobProxy proxy;
    private final ObjectMapper objectMapper;

    private int currentPageNumber = 1;

    public void navigateToRidesHomePage() {

        cleanUpExistingNetworkLogs();

        currentPageNumber = 1;
        navigateToPage(currentPageNumber);
    }

    public void navigateToRidesNextPage() {

        int maxPageNumber = getMaxPageNumber();

        if (currentPageNumber >= maxPageNumber) {
            getLogger().warn("Cannot navigate to next page, currentPage [{}], maxPage [{}]", currentPageNumber, maxPageNumber);
            return;
        }

        cleanUpExistingNetworkLogs();

        currentPageNumber++;
        navigateToPage(currentPageNumber);
    }

    public boolean isLastPage() {

        return currentPageNumber == getMaxPageNumber();
    }

    public List<Ride> getRides() {

        return proxy.getHar().getLog().getEntries().stream()
                .filter(BananacarPage::isRidesSearchRequest)
                .map(h -> h.getResponse().getContent().getText())
                .peek(response -> {
                    if (response == null) {
                        getLogger().warn("Cannot getRides because RidesSearchResponse is null");
                    }
                })
                .filter(Objects::nonNull)
                .map(this::parseBananacarRideSearchResponse)
                .map(BananacarRideSearchResponse::getRides)
                .flatMap(Collection::stream)
                .map(this::toRides)
                .toList();
    }

    private int getMaxPageNumber() {

        return proxy.getHar().getLog().getEntries().stream()
                .filter(BananacarPage::isRidesSearchRequest)
                .map(h -> h.getResponse().getContent().getText())
                .filter(Objects::nonNull)
                .findFirst()
                .map(this::parseBananacarRideSearchResponse)
                .map(BananacarRideSearchResponse::getLastPage)
                .orElseThrow(() -> new IllegalArgumentException("Cannot getMaxPageNumber because RidesSearchResponse is null"));
    }

    private void navigateToPage(int pageNumber) {

        webDriver.navigate().to(RIDES_URL_TEMPLATE.formatted(pageNumber));

        forceWait(Duration.ofMillis(200));
    }

    private Ride toRides(BananacarRide ride) {

        try {

            List<Location> locations = mapRoute(ride.getLocations());
            URL bananacarUrl = new URL(RIDE_URL_TEMPLATE.formatted(ride.getUrl()));
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

        return har.getRequest().getUrl().contains(RIDES_SEARCH_API_PATH);
    }

    private BananacarRideSearchResponse parseBananacarRideSearchResponse(String responseBody) {

        try {
            return objectMapper.readValue(responseBody, BananacarRideSearchResponse.class);
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
