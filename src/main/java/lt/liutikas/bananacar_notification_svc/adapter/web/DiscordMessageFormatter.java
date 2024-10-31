package lt.liutikas.bananacar_notification_svc.adapter.web;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lt.liutikas.bananacar_notification_svc.domain.Ride;
import lt.liutikas.bananacar_notification_svc.domain.RideSubscription;

import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DiscordMessageFormatter {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static String formatPushNotification(Ride ride) {

        return """
                Departure: %s
                From: %s
                To: %s
                                
                """
                .formatted(
                        ride.getDepartsOn().format(DATE_TIME_FORMATTER),
                        ride.getOrigin().getCity(),
                        ride.getDestination().getCity()
                );
    }

    public static String formatDecoratedMessage(Ride ride) {

        return """
                > **Departure**: `%s`
                > **From**: %s
                > **To**: %s
                                
                """
                .formatted(
                        ride.getDepartsOn().format(DATE_TIME_FORMATTER),
                        ride.getOrigin().getCity(),
                        ride.getDestination().getCity()
                );
    }

    public static String formatPushNotification(RideSubscription subscription) {

        return """
                ID: %s
                From: %s
                To: %s
                                
                """
                .formatted(
                        subscription.getId(),
                        subscription.getOriginCity(),
                        subscription.getDestinationCity()
                );
    }

    public static String formatDecoratedMessage(RideSubscription subscription) {

        return """
                > **ID**: `%s`
                > **From**: %s
                > **To**: %s
                                
                """
                .formatted(
                        subscription.getId(),
                        subscription.getOriginCity(),
                        subscription.getDestinationCity()
                );
    }
}
