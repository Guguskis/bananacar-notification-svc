package lt.liutikas.bananacar_notification_svc.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Location {

    final String city;
    final LocationType type;
}
