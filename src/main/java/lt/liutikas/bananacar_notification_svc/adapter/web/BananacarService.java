package lt.liutikas.bananacar_notification_svc.adapter.web;

import lt.liutikas.bananacar_notification_svc.application.port.in.FetchLatestRidesPort;
import lt.liutikas.bananacar_notification_svc.domain.Ride;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BananacarService implements FetchLatestRidesPort {

    @Override
    public List<Ride> fetch(LocalDateTime maximumDepartsOn) {

        // todo
        return null;
    }
}
