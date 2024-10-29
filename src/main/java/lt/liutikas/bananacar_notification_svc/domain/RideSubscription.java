package lt.liutikas.bananacar_notification_svc.domain;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class RideSubscription {

    final UUID subscriptionId;
    final String originCity;
    final String destinationCity;
    final LocalDateTime departsOnEarliest;
    final LocalDateTime departsOnLatest;

}
