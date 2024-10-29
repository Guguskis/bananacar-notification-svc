package lt.liutikas.bananacar_notification_svc.adapter.persistence;

import lt.liutikas.bananacar_notification_svc.application.port.in.FetchRidesByRideIdPort;
import lt.liutikas.bananacar_notification_svc.application.port.out.SaveRidesPort;
import lt.liutikas.bananacar_notification_svc.domain.Ride;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RidesRepository implements FetchRidesByRideIdPort, SaveRidesPort {

    @Override
    public List<Ride> fetch(List<String> rideIds) {

        // todo
        return List.of();
    }

    @Override
    public void save(List<Ride> rides) {

        // todo
    }
}
