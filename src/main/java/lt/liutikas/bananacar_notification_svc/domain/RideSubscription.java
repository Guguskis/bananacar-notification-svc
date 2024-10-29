package lt.liutikas.bananacar_notification_svc.domain;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RideSubscription {

    final String userId;
    final String originCity;
    final String destinationCity;
    final LocalDateTime departsOnEarliest;
    final LocalDateTime departsOnLatest;

}
