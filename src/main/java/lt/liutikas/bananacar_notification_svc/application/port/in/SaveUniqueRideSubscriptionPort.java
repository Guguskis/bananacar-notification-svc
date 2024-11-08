package lt.liutikas.bananacar_notification_svc.application.port.in;

import lombok.Builder;
import lombok.Data;
import lt.liutikas.bananacar_notification_svc.domain.RideSubscription;

import java.time.LocalDateTime;

public interface SaveUniqueRideSubscriptionPort {

    RideSubscription create(CreateCommand command);

    @Data
    @Builder
    class CreateCommand {

        private final String originCity;
        private final String destinationCity;
        private final LocalDateTime departsOnEarliest;
        private final LocalDateTime departsOnLatest;
    }
}
