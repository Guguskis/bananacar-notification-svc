package lt.liutikas.bananacar_notification_svc.domain;

import java.net.URL;
import java.time.LocalDateTime;

public record Ride(
        String rideId,
        String originCity,
        String destinationCity,
        LocalDateTime departsOn,
        URL bananacarUrl
) {
}
