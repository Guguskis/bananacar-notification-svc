package lt.liutikas.bananacar_notification_svc.application.port.in;

import lt.liutikas.bananacar_notification_svc.domain.Ride;

import java.time.LocalDateTime;
import java.util.List;

public interface FetchLatestRidesPort {

    List<Ride> fetch(LocalDateTime maximumDepartsOn);
}
