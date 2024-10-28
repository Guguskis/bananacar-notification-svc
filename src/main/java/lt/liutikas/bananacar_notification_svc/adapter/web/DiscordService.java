package lt.liutikas.bananacar_notification_svc.adapter.web;

import lt.liutikas.bananacar_notification_svc.application.port.out.NotificationPort;
import org.springframework.stereotype.Service;

import java.net.URL;

@Service
public class DiscordService implements NotificationPort {

    @Override
    public void notifyUser(String userId, String message, URL bananacarUrl) {

        // todo
    }
}
