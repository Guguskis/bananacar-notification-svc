package lt.liutikas.bananacar_notification_svc.application;

import lombok.RequiredArgsConstructor;
import lt.liutikas.bananacar_notification_svc.application.port.in.FetchRideSubscriptionsPort;
import lt.liutikas.bananacar_notification_svc.application.port.in.SaveRideSubscriptionPort;
import lt.liutikas.bananacar_notification_svc.common.Loggable;
import lt.liutikas.bananacar_notification_svc.domain.RideSubscription;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static lt.liutikas.bananacar_notification_svc.common.StringUtils.removeDiacriticalMarks;

@Service
@RequiredArgsConstructor
public class SaveRideSubscriptionUseCase implements Loggable, SaveRideSubscriptionPort {

    private final FetchRideSubscriptionsPort fetchRideSubscriptionsPort;
    private final CreateRideSubscriptionPort createRideSubscriptionPort; // fixme bad design, I have Save and Create Ports

    @Override
    public RideSubscription create(CreateCommand command) {

        return fetchRideSubscriptionsPort.fetch().stream()
                .filter(sub -> matchingCities(sub, command))
                .findFirst()
                .orElseGet(() -> createRideSubscription(command));
    }

    private RideSubscription createRideSubscription(CreateCommand command) {

        LocalDateTime now = LocalDateTime.now();

        RideSubscription rideSubscription = createRideSubscriptionPort.create(
                RideSubscription.builder()
                        .originCity(command.getOriginCity())
                        .destinationCity(command.getDestinationCity())
                        .departsOnEarliest(command.getDepartsOnEarliest())
                        .departsOnLatest(command.getDepartsOnLatest())
                        .createdOn(now)
                        .updatedOn(now)
                        .build()
        );

        getLogger().info("Saved ride subscription from [{}] to [{}] with id [{}]",
                rideSubscription.getOriginCity(),
                rideSubscription.getDestinationCity(),
                rideSubscription.getId());

        return rideSubscription;
    }

    public boolean matchingCities(RideSubscription subscription, CreateCommand command) {

        String originCity = removeDiacriticalMarks(command.getOriginCity());
        String destinationCity = removeDiacriticalMarks(command.getDestinationCity());

        return subscription.getOriginCity().equalsIgnoreCase(originCity)
                && subscription.getDestinationCity().equalsIgnoreCase(destinationCity);
    }

}
