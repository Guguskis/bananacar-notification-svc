package lt.liutikas.bananacar_notification_svc.adapter.web;

import lt.liutikas.bananacar_notification_svc.adapter.web.bananacar.BananacarService;
import lt.liutikas.bananacar_notification_svc.application.ScanRidesUseCase;
import lt.liutikas.bananacar_notification_svc.domain.Ride;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

@ActiveProfiles({"itest", "secrets"})
class BananacarServiceITest extends ITestBase {

    @Autowired
    private BananacarService bananacarService;
    @Autowired
    private ScanRidesUseCase scanRidesUseCase;

    @Test
    void triggerFetch() {

        List<Ride> rides = bananacarService.fetch(LocalDateTime.now().plusDays(2));

        for (Ride ride : rides) {
            System.out.println(ride);
        }
    }

    @Test
    void triggerScan() {

        scanRidesUseCase.scan();
    }

}