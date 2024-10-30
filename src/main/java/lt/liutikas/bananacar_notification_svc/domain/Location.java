package lt.liutikas.bananacar_notification_svc.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Location {

    private final String city;
    private final LocationType type;
}
