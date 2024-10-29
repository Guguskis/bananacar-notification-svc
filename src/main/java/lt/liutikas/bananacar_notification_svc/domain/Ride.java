package lt.liutikas.bananacar_notification_svc.domain;

import lombok.Builder;
import lombok.Data;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class Ride {

    final String rideId;
    final List<Location> locations;
    final LocalDateTime departsOn;
    final URL bananacarUrl;

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

        return locations.stream()
                .filter(l -> l.getType() == LocationType.ORIGIN)
                .anyMatch(l -> l.getCity().equalsIgnoreCase(city));
    }

    private boolean visits(String city) {

        return locations.stream()
                .filter(l -> l.getType() != LocationType.ORIGIN)
                .anyMatch(l -> l.getCity().equalsIgnoreCase(city));
    }
}
