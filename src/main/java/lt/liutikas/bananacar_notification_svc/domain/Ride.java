package lt.liutikas.bananacar_notification_svc.domain;

import lombok.Builder;
import lombok.Data;

import java.net.URL;
import java.time.LocalDateTime;

@Data
@Builder
public class Ride {

    final String rideId;
    final Route route;
    final LocalDateTime departsOn;
    final URL bananacarUrl;
}
