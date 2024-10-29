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
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class BananacarPage implements Loggable {

    private static final String BANANACAR_BASE_URL = "https://bananacar.lt/en";
    private static final String RIDES_HOME_PAGE_URL = BANANACAR_BASE_URL + "/ride/find";
    private static final String NEXT_BUTTON_CSS_SELECTOR = "i.mdi.mdi-chevron-right.find__pagination-nav";

    private final WebDriver firefoxWebDriver;
    private final BrowserMobProxy proxy;
    private final ObjectMapper objectMapper;

    public int navigateToRidesHomePage() {

        cleanUpExistingNetworkLogs();
        firefoxWebDriver.navigate().to(RIDES_HOME_PAGE_URL);

        return 1;
    }

    public int navigateToRidesNextPage() {

        cleanUpExistingNetworkLogs();

        WebElement nextButton = firefoxWebDriver.findElement(By.cssSelector(NEXT_BUTTON_CSS_SELECTOR));

        forceWait(Duration.ofMillis(10));
        String url = clickAndWaitForUrlChange(firefoxWebDriver, nextButton);

        return extractPageNumber(url);
    }

    public List<Ride> getRides() {

        return proxy.getHar().getLog().getEntries().stream()
                .filter(BananacarPage::isRidesSearchRequest)
                .map(h -> h.getResponse().getContent().getText())
                .map(this::parseBananacarRideSearchResponse)
                .map(BananacarRideSearchResponse::getRides)
                .flatMap(Collection::stream)
                .map(this::toRides)
                .toList();
    }

    private Ride toRides(BananacarRide ride) {

        try {

            List<Location> locations = mapRoute(ride.getLocations());
            URL bananacarUrl = new URL(BANANACAR_BASE_URL + "/ride/find?ride_id=" + ride.getId());
            LocalDateTime departureDatetime = ride.getDepartureDatetime();

            return Ride.builder()
                    .rideId(ride.getId())
                    .locations(locations)
                    .departsOn(departureDatetime)
                    .bananacarUrl(bananacarUrl)
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

    private LocationType resolveLocationType(int currentLocation, int locationsCount) {

        if (currentLocation == 0) {
            return LocationType.ORIGIN;
        } else if (currentLocation == locationsCount - 1) {
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

    private void forceWait(Duration timeout) {

        try {
            Thread.sleep(timeout.toMillis());
        } catch (InterruptedException e) {
            getLogger().error("Thread sleep error", e);
        }
    }

    public Integer extractPageNumber(String url) {
        try {
            Pattern pattern = Pattern.compile("page=(\\d+)");
            Matcher matcher = pattern.matcher(url);
            if (matcher.find()) {
                return Integer.parseInt(matcher.group(1));
            }
        } catch (Exception e) {
            getLogger().warn("Failed to parse page number for [{}]", url, e);
        }

        return 1;
    }

    public String clickAndWaitForUrlChange(WebDriver driver, WebElement button) {

        String initialUrl = driver.getCurrentUrl();

        button.click();

        new WebDriverWait(driver, Duration.ofSeconds(10)).until(
                ExpectedConditions.not(ExpectedConditions.urlToBe(initialUrl))
        );

        return firefoxWebDriver.getCurrentUrl();
    }

    private BananacarRideSearchResponse parseBananacarRideSearchResponse(String responseBody) {

        try {
            return objectMapper.readValue(responseBody, BananacarRideSearchResponse.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to parse BananacarRideSearchResponse", e);
        }
    }
}
