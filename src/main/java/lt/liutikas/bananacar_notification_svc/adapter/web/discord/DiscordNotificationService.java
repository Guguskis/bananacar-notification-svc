package lt.liutikas.bananacar_notification_svc.adapter.web.discord;

import lombok.RequiredArgsConstructor;
import lt.liutikas.bananacar_notification_svc.application.port.out.*;
import lt.liutikas.bananacar_notification_svc.common.Loggable;
import lt.liutikas.bananacar_notification_svc.domain.Ride;
import lt.liutikas.bananacar_notification_svc.domain.RideSubscription;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import static lt.liutikas.bananacar_notification_svc.adapter.web.discord.DiscordMessageFormatter.formatDecoratedMessage;
import static lt.liutikas.bananacar_notification_svc.adapter.web.discord.DiscordMessageFormatter.formatPushNotification;

@Service
@RequiredArgsConstructor
public class DiscordNotificationService implements
        Loggable,
        NotifyNewRidePort,
        RespondSubscriptionListPort,
        RespondSubscriptionCreatedPort,
        RespondSubscriptionDeletedPort,
        RespondSubscriptionNotFoundPort {

    private static final String HEADER_SUBSCRIPTIONS_LIST = "Listing All Ride Subscriptions\n";
    private static final String HEADER_SUBSCRIPTION_CREATED = "Created Ride Subscription\n";
    private static final String HEADER_SUBSCRIPTION_DELETED = "Deleted Ride Subscription\n";
    private static final String HEADER_RIDE_CREATED = "New ride appeared\n";
    private static final String RESPONSE_SUBSCRIPTIONS_EMPTY = "There are no ride subscriptions, maybe create one?";
    private static final String RESPONSE_SUBSCRIPTION_NOT_FOUND = "Subscription not found";

    private final ServerTextChannel channel;

    @Override
    public void notify(Ride ride) {

        getLogger().info("Sending new ride notification");

        new MessageBuilder()
                .addComponents(linkButton(ride.getBananacarUrl()))
                .setContent(HEADER_RIDE_CREATED + formatPushNotification(ride))
                .send(channel)
                .thenAcceptAsync(action -> action
                        .edit(appendHeader(HEADER_RIDE_CREATED) + formatDecoratedMessage(ride))
                        .join()
                )
                .join();
    }

    @Override
    public void respondList(SlashCommandInteraction interaction, List<RideSubscription> subscriptions) {

        if (subscriptions.isEmpty()) {
            respondListSubscriptionsEmpty(interaction);
        } else {
            respondListSubscriptions(interaction, subscriptions);
        }
    }

    @Override
    public void respondCreated(SlashCommandInteraction interaction, RideSubscription rideSubscription) {

        interaction.createImmediateResponder()
                .setContent(HEADER_SUBSCRIPTION_CREATED)
                .append(formatPushNotification(rideSubscription))
                .respond()
                .thenAcceptAsync(action -> action
                        .setContent(appendHeader(HEADER_SUBSCRIPTION_CREATED))
                        .append(formatDecoratedMessage(rideSubscription))
                        .update()
                        .join())
                .join();
    }

    @Override
    public void respondDeleted(SlashCommandInteraction interaction, RideSubscription rideSubscription) {

        interaction.createImmediateResponder()
                .setContent(HEADER_SUBSCRIPTION_DELETED)
                .append(formatPushNotification(rideSubscription))
                .respond()
                .thenAcceptAsync(action -> action
                        .setContent(appendHeader(HEADER_SUBSCRIPTION_DELETED))
                        .append(formatDecoratedMessage(rideSubscription))
                        .update()
                        .join())
                .join();

    }

    @Override
    public void respondDeletedNotFound(SlashCommandInteraction interaction) {

        interaction.createImmediateResponder()
                .setContent(HEADER_SUBSCRIPTION_DELETED)
                .append(RESPONSE_SUBSCRIPTION_NOT_FOUND)
                .respond()
                .thenAcceptAsync(action -> action
                        .setContent(appendHeader(HEADER_SUBSCRIPTION_DELETED))
                        .append(RESPONSE_SUBSCRIPTION_NOT_FOUND)
                        .update()
                        .join())
                .join();
    }

    private void respondListSubscriptionsEmpty(SlashCommandInteraction interaction) {

        interaction.createImmediateResponder()
                .setContent(HEADER_SUBSCRIPTIONS_LIST)
                .append(RESPONSE_SUBSCRIPTIONS_EMPTY)
                .respond()
                .thenAcceptAsync(message -> message
                        .setContent(appendHeader(HEADER_SUBSCRIPTIONS_LIST))
                        .append(RESPONSE_SUBSCRIPTIONS_EMPTY)
                        .update()
                        .join())
                .join();
    }

    private void respondListSubscriptions(SlashCommandInteraction interaction, List<RideSubscription> subscriptions) {

        interaction.createImmediateResponder()
                .setContent(HEADER_SUBSCRIPTIONS_LIST)
                .append(subscriptions.stream()
                        .map(DiscordMessageFormatter::formatPushNotification)
                        .collect(Collectors.joining()))
                .respond()
                .thenAcceptAsync(message -> message
                        .setContent(appendHeader(HEADER_SUBSCRIPTIONS_LIST))
                        .append(subscriptions.stream()
                                .map(DiscordMessageFormatter::formatDecoratedMessage)
                                .collect(Collectors.joining()))
                        .update()
                        .join())
                .join();
    }

    private String appendHeader(String text) {

        return "## " + text;
    }

    private static ActionRow linkButton(URL url) {

        return ActionRow.of(
                Button.link(url.toString(), "BananaCar")
        );
    }

}
