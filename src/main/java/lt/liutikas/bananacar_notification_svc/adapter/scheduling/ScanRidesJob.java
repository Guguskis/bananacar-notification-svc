package lt.liutikas.bananacar_notification_svc.adapter.scheduling;

import lombok.RequiredArgsConstructor;
import lt.liutikas.bananacar_notification_svc.application.ScanRidesUseCase;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@RequiredArgsConstructor
public class ScanRidesJob {

    private final ScanRidesUseCase scanRidesUseCase;

    @Scheduled(cron = "0 */5 * * * ?")
    public void scanRides() {

        scanRidesUseCase.scan();
    }
}
