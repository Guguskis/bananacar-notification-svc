package lt.liutikas.bananacar_notification_svc.adapter.web;

import lt.liutikas.bananacar_notification_svc.domain.Ride;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

class BananacarServiceITest extends ITestBase {

    @Autowired
    private BananacarService bananacarService;

    @Test
    void test() {

        List<Ride> rides = bananacarService.fetch(LocalDateTime.now().plusDays(1));

        for (Ride ride : rides) {
            System.out.println(ride);
        }
    }

}