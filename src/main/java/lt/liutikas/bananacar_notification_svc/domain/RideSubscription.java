package lt.liutikas.bananacar_notification_svc.domain;

import java.time.LocalDateTime;

public record RideSubscription(
        String userId,
        String originCity,
        String destinationCity,
        LocalDateTime departsOnEarliest,
        LocalDateTime departsOnLatest
) {
}
