package lt.liutikas.bananacar_notification_svc.adapter.web.discord;

import lombok.RequiredArgsConstructor;
import lt.liutikas.bananacar_notification_svc.application.port.in.DeleteRideSubscriptionPort;
import lt.liutikas.bananacar_notification_svc.application.port.out.RespondSubscriptionListPort;
import lt.liutikas.bananacar_notification_svc.common.util.Loggable;
import lt.liutikas.bananacar_notification_svc.domain.RideSubscription;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.event.interaction.ButtonClickEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.listener.interaction.ButtonClickListener;
import org.javacord.api.util.event.ListenerManager;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;

import static lt.liutikas.bananacar_notification_svc.adapter.web.discord.DiscordMessageFormatter.*;

@Component
@RequiredArgsConstructor
public class DiscordRespondSubscriptionListService implements Loggable, RespondSubscriptionListPort {

    private static final String DELETE_SUBSCRIPTION_PREFIX = "delete_subscription_";
    private static final String MESSAGE_SUBSCRIPTION_DELETED = "Subscription deleted";
    private static final String MESSAGE_SUBSCRIPTION_ALREADY_DELETED = "Subscription was already deleted";

    private final DiscordApi discordApi;
    private final DeleteRideSubscriptionPort deleteRideSubscriptionPort;
    private ListenerManager<ButtonClickListener> buttonClickListenerListenerManager;

    @PostConstruct
    public void setupListeners() {

        buttonClickListenerListenerManager = discordApi.addButtonClickListener(this::handleButtonClick);
    }

    @PreDestroy
    public void cleanupListeners() {

        if (buttonClickListenerListenerManager != null) {
            buttonClickListenerListenerManager.remove();
        }
    }

    @Override
    public void respondList(SlashCommandInteraction interaction, List<RideSubscription> subscriptions) {

        if (subscriptions.isEmpty()) {
            respondListSubscriptionsEmpty(interaction);
        } else {
            respondListSubscriptions(interaction, subscriptions);
        }
    }

    private void handleButtonClick(ButtonClickEvent event) {

        Integer subscriptionId = parseSubscriptionId(event);

        if (subscriptionId == null) return;

        String message = deleteRideSubscriptionPort.delete(subscriptionId)
                .map(s -> MESSAGE_SUBSCRIPTION_DELETED)
                .orElse(MESSAGE_SUBSCRIPTION_ALREADY_DELETED);

        event.getButtonInteraction()
                .createOriginalMessageUpdater()
                .setContent(message)
                .update();
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
                        DELETE_SUBSCRIPTION_PREFIX + subscription.getId(),
                        "Delete"
                )
        );
    }

    private Integer parseSubscriptionId(ButtonClickEvent event) {

        String customId = event.getButtonInteraction().getCustomId();

        if (!customId.startsWith(DELETE_SUBSCRIPTION_PREFIX)) {
            return null;
        }

        String subscriptionIdString = customId.replace(DELETE_SUBSCRIPTION_PREFIX, "");

        return Integer.parseInt(subscriptionIdString);
    }
}
