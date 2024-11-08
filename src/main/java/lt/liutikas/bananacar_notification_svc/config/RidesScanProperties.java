package lt.liutikas.bananacar_notification_svc.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class RidesScanProperties {

    @Value("${rides.scan.maximum.departureInDays:2}")
    private int maximumDepartureInDays;
}
