package lt.liutikas.bananacar_notification_svc.adapter.web.discord;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lt.liutikas.bananacar_notification_svc.domain.Ride;
import lt.liutikas.bananacar_notification_svc.domain.RideSubscription;

import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DiscordMessageFormatter {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private static final String HEADER_RIDE_CREATED = "New ride appeared";
    private static final String HEADER_SUBSCRIPTION_CREATED = "Created Ride Subscription";
    private static final String HEADER_SUBSCRIPTION_DELETED = "Deleted Ride Subscription";
    private static final String RESPONSE_SUBSCRIPTION_NOT_FOUND = "Subscription not found";
    private static final String HEADER_SUBSCRIPTIONS_LIST = "Listing All Ride Subscriptions";
    private static final String RESPONSE_SUBSCRIPTIONS_EMPTY = "There are no ride subscriptions, maybe create one?";

    public static String toDecorated(Ride ride) {

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

    public static String toDecorated(RideSubscription subscription) {

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

    public static String toPlain(Ride ride) {

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

    public static String toPlain(RideSubscription subscription) {

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

    static String toPlainRideCreatedMessage(Ride ride) {

        return """
                %s
                %s
                """.formatted(HEADER_RIDE_CREATED, toPlain(ride));
    }

    static String toPlainSubscriptionCreatedMessage() {

        return """
                %s
                """.formatted(HEADER_SUBSCRIPTION_CREATED);
    }

    static String toPlainSubscriptionDeletedMessage(RideSubscription rideSubscription) {

        return """
                %s
                %s
                """.formatted(HEADER_SUBSCRIPTION_DELETED, toPlain(rideSubscription));
    }

    static String toPlainSubscriptionDeletedMessage() {

        return """
                %s
                %s
                """.formatted(HEADER_SUBSCRIPTION_DELETED, RESPONSE_SUBSCRIPTION_NOT_FOUND);
    }

    static String toPlainSubscriptionsListEmptyMessage() {

        return """
                %s
                %s
                """.formatted(HEADER_SUBSCRIPTIONS_LIST, RESPONSE_SUBSCRIPTIONS_EMPTY);
    }


    static String toPlainSubscriptionsListMessage() {

        return "%s".formatted(HEADER_SUBSCRIPTIONS_LIST);
    }

    static String toDecoratedRideCreatedMessage(Ride ride) {

        return """
                ## %s
                %s
                """.formatted(HEADER_RIDE_CREATED, toDecorated(ride));
    }

    static String toDecoratedSubscriptionCreatedMessage() {

        return """
                ## %s
                """.formatted(HEADER_SUBSCRIPTION_CREATED);
    }

    static String toDecoratedSubscriptionDeletedMessage(RideSubscription rideSubscription) {

        return """
                ## %s
                %s
                """.formatted(HEADER_SUBSCRIPTION_DELETED, toDecorated(rideSubscription));
    }

    static String toDecoratedSubscriptionDeletedMessage() {

        return """
                ## %s
                %s
                """.formatted(HEADER_SUBSCRIPTION_DELETED, RESPONSE_SUBSCRIPTION_NOT_FOUND);
    }

    static String toDecoratedSubscriptionsListEmptyMessage() {

        return """
                ## %s
                %s
                """.formatted(HEADER_SUBSCRIPTIONS_LIST, RESPONSE_SUBSCRIPTIONS_EMPTY);
    }

    static String toDecoratedSubscriptionsListMessage() {

        return "## %s".formatted(HEADER_SUBSCRIPTIONS_LIST);
    }
}
