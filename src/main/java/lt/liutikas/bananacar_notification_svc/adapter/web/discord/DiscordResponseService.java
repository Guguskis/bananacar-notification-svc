package lt.liutikas.bananacar_notification_svc.adapter.web.discord;

import lombok.RequiredArgsConstructor;
import lt.liutikas.bananacar_notification_svc.application.port.out.RespondSubscriptionCreatedPort;
import lt.liutikas.bananacar_notification_svc.application.port.out.RespondSubscriptionDeletedPort;
import lt.liutikas.bananacar_notification_svc.application.port.out.RespondSubscriptionListPort;
import lt.liutikas.bananacar_notification_svc.application.port.out.RespondSubscriptionNotFoundPort;
import lt.liutikas.bananacar_notification_svc.common.util.Loggable;
import lt.liutikas.bananacar_notification_svc.domain.RideSubscription;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
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
                .setContent(toPlainSubscriptionsListMessage())
                .respond()
                .thenAcceptAsync(responseMessage -> {
                    responseMessage
                            .setContent(toDecoratedSubscriptionsListMessage())
                            .update();

                    sendIndividualMessagesWithDeleteButton(interaction, subscriptions);
                });
    }

    private void sendIndividualMessagesWithDeleteButton(SlashCommandInteraction interaction, List<RideSubscription> subscriptions) {

        TextChannel channel = interaction.getChannel()
                .orElseThrow(() -> new IllegalStateException("Failed to acquire discord channel"));

        for (RideSubscription subscription : subscriptions) {

            String plainMessage = toPlain(subscription);
            String decoratedMessage = toDecorated(subscription);
            ActionRow deleteButton = createDeleteButton(subscription);

            new MessageBuilder()
                    .setContent(plainMessage)
                    .addComponents(deleteButton)
                    .send(channel)
                    .thenAcceptAsync(message -> message.edit(decoratedMessage));
        }
    }

    private ActionRow createDeleteButton(RideSubscription subscription) {

        return ActionRow.of(
                Button.danger(
                        "delete_subscription_" + subscription.getId(),
                        "Delete ID: " + subscription.getId()
                )
        );
    }
}
