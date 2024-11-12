package lt.liutikas.bananacar_notification_svc.adapter.web.discord;

import lombok.RequiredArgsConstructor;
import lt.liutikas.bananacar_notification_svc.application.port.out.*;
import lt.liutikas.bananacar_notification_svc.common.util.Loggable;
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

import static lt.liutikas.bananacar_notification_svc.adapter.web.discord.DiscordMessageFormatter.*;

@Service
@RequiredArgsConstructor
public class DiscordNotificationService implements
        Loggable,
        NotifyNewRidePort,
        RespondSubscriptionListPort,
        RespondSubscriptionCreatedPort,
        RespondSubscriptionDeletedPort,
        RespondSubscriptionNotFoundPort {

    private final ServerTextChannel channel;

    @Override
    public void notify(Ride ride) {

        getLogger().info("Sending new ride notification");

        new MessageBuilder()
                .addComponents(linkButton(ride.getBananacarUrl()))
                .setContent(toPlainRideCreatedMessage(ride))
                .send(channel)
                .thenAcceptAsync(action -> action
                        .edit(toDecoratedRideCreatedMessage(ride))
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
                .setContent(toPlainSubscriptionCreatedMessage(rideSubscription))
                .respond()
                .thenAcceptAsync(action -> action
                        .setContent(toDecoratedSubscriptionCreatedMessage(rideSubscription))
                        .update()
                        .join())
                .join();
    }

    @Override
    public void respondDeleted(SlashCommandInteraction interaction, RideSubscription rideSubscription) {

        interaction.createImmediateResponder()
                .setContent(toPlainSubscriptionDeletedMessage(rideSubscription))
                .respond()
                .thenAcceptAsync(action -> action
                        .setContent(toDecoratedSubscriptionDeletedMessage(rideSubscription))
                        .update()
                        .join())
                .join();

    }

    @Override
    public void respondDeletedNotFound(SlashCommandInteraction interaction) {

        interaction.createImmediateResponder()
                .setContent(toPlainSubscriptionDeletedMessage())
                .respond()
                .thenAcceptAsync(action -> action
                        .setContent(toDecoratedSubscriptionDeletedMessage())
                        .update()
                        .join())
                .join();
    }

    private void respondListSubscriptionsEmpty(SlashCommandInteraction interaction) {

        interaction.createImmediateResponder()
                .setContent(toPlainSubscriptionsListEmptyMessage())
                .respond()
                .thenAcceptAsync(message -> message
                        .setContent(toDecoratedSubscriptionsListEmptyMessage())
                        .update()
                        .join())
                .join();
    }

    private void respondListSubscriptions(SlashCommandInteraction interaction, List<RideSubscription> subscriptions) {

        interaction.createImmediateResponder()
                .setContent(toPlainSubscriptionsListMessage(subscriptions))
                .respond()
                .thenAcceptAsync(message -> message
                        .setContent(toDecoratedSubscriptionsListMessage(subscriptions))
                        .update()
                        .join())
                .join();
    }

    private static ActionRow linkButton(URL url) {

        return ActionRow.of(
                Button.link(url.toString(), "BananaCar")
        );
    }

}
