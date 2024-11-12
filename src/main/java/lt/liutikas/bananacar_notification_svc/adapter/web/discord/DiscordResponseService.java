package lt.liutikas.bananacar_notification_svc.adapter.web.discord;

import lombok.RequiredArgsConstructor;
import lt.liutikas.bananacar_notification_svc.application.port.out.RespondSubscriptionCreatedPort;
import lt.liutikas.bananacar_notification_svc.application.port.out.RespondSubscriptionDeletedPort;
import lt.liutikas.bananacar_notification_svc.application.port.out.RespondSubscriptionListPort;
import lt.liutikas.bananacar_notification_svc.application.port.out.RespondSubscriptionNotFoundPort;
import lt.liutikas.bananacar_notification_svc.common.util.Loggable;
import lt.liutikas.bananacar_notification_svc.domain.RideSubscription;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.springframework.stereotype.Service;

import java.util.List;

import static lt.liutikas.bananacar_notification_svc.adapter.web.discord.DiscordMessageFormatter.*;

@Service
@RequiredArgsConstructor
public class DiscordResponseService implements
        Loggable,
        RespondSubscriptionListPort,
        RespondSubscriptionCreatedPort,
        RespondSubscriptionDeletedPort,
        RespondSubscriptionNotFoundPort {

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
}
