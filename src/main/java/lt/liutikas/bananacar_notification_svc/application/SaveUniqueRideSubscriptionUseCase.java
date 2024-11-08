package lt.liutikas.bananacar_notification_svc.application;

import lombok.RequiredArgsConstructor;
import lt.liutikas.bananacar_notification_svc.application.port.in.CreateRideSubscriptionPort;
import lt.liutikas.bananacar_notification_svc.application.port.in.FetchRideSubscriptionsPort;
import lt.liutikas.bananacar_notification_svc.application.port.in.SaveUniqueRideSubscriptionPort;
import lt.liutikas.bananacar_notification_svc.common.util.Loggable;
import lt.liutikas.bananacar_notification_svc.domain.RideSubscription;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static lt.liutikas.bananacar_notification_svc.common.util.StringUtils.removeDiacriticalMarks;

@Service
@RequiredArgsConstructor
public class SaveUniqueRideSubscriptionUseCase implements Loggable, SaveUniqueRideSubscriptionPort {

    private final FetchRideSubscriptionsPort fetchRideSubscriptionsPort;
    private final CreateRideSubscriptionPort createRideSubscriptionPort;

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

        getLogger().info("Created ride subscription from [{}] to [{}] with id [{}]",
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
