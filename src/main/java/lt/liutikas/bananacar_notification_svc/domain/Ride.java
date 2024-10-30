package lt.liutikas.bananacar_notification_svc.domain;

import lombok.Builder;
import lombok.Data;

import java.net.URL;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class Ride {

    private final UUID id;
    private final String bananacarRideId;
    private final List<Location> locations;
    private final LocalDateTime departsOn;
    private final URL bananacarUrl;
    private final LocalDateTime createdOn;
    private final LocalDateTime updatedOn;

    public boolean isRouteMatch(String originCity, String destinationCity) {

        return isOrigin(originCity) && visits(destinationCity);
    }

    public Location getOrigin() {

        return locations.stream()
                .filter(l -> l.getType() == LocationType.ORIGIN)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Route has no origin"));
    }

    public Location getDestination() {

        return locations.stream()
                .filter(l -> l.getType() == LocationType.DESTINATION)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Route has no destination"));
    }

    private boolean isOrigin(String city) {

        String normalisedCity = removeDiacriticalMarks(city);

        return locations.stream()
                .map(Location::getCity)
                .map(this::removeDiacriticalMarks)
                .anyMatch(l -> l.equalsIgnoreCase(normalisedCity));
    }

    private boolean visits(String city) {

        String normalisedCity = removeDiacriticalMarks(city);

        return locations.stream()
                .filter(l -> l.getType() != LocationType.ORIGIN)
                .map(Location::getCity)
                .map(this::removeDiacriticalMarks)
                .anyMatch(l -> l.equalsIgnoreCase(normalisedCity));
    }

    private String removeDiacriticalMarks(String input) {

        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "");
    }
}
