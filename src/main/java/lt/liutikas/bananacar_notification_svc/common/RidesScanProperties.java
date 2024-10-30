package lt.liutikas.bananacar_notification_svc.common;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

@Data
public class RidesScanProperties {

    @Value("${rides.scan.maximum.departureInDays}")
    private final int maximumDepartureInDays;
}
