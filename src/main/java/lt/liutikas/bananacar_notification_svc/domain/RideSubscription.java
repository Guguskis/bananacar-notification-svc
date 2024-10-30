package lt.liutikas.bananacar_notification_svc.domain;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class RideSubscription {

    private final UUID subscriptionId;
    private final String originCity;
    private final String destinationCity;
    private final LocalDateTime departsOnEarliest;
    private final LocalDateTime departsOnLatest;
    private final LocalDateTime createdOn;
    private final LocalDateTime updatedOn;

}
