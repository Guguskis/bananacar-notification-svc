package lt.liutikas.bananacar_notification_svc.domain;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RideSubscription {

    private final int id;
    private final String originCity;
    private final String destinationCity;
    private final LocalDateTime departsOnEarliest;
    private final LocalDateTime departsOnLatest;
    private final LocalDateTime createdOn;
    private final LocalDateTime updatedOn;

}
