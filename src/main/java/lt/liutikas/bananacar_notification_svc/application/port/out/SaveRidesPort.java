package lt.liutikas.bananacar_notification_svc.application.port.out;

import lt.liutikas.bananacar_notification_svc.domain.Ride;

import java.util.List;

public interface SaveRidesPort {

    void save(List<Ride> rides);
}
