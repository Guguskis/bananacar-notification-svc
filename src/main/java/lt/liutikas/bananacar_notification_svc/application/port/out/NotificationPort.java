package lt.liutikas.bananacar_notification_svc.application.port.out;

import lt.liutikas.bananacar_notification_svc.domain.Ride;

public interface NotificationPort {

    void notify(Ride ride);
}
