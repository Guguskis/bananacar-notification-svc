package lt.liutikas.bananacar_notification_svc.application.port.out;

import java.net.URL;

public interface NotificationPort {

    void notifyUser(String userId, String message, URL bananacarUrl);
}
