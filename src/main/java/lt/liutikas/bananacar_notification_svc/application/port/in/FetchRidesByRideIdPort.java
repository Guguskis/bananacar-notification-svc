package lt.liutikas.bananacar_notification_svc.application.port.in;

import lt.liutikas.bananacar_notification_svc.domain.Ride;

import java.util.List;

public interface FetchRidesByRideIdPort {

    List<Ride> fetch(List<String> rideIds);
}
