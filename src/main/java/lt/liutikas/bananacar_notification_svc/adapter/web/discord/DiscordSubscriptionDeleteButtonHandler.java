package lt.liutikas.bananacar_notification_svc.adapter.web.discord;

import lombok.RequiredArgsConstructor;
import lt.liutikas.bananacar_notification_svc.application.port.in.DeleteRideSubscriptionPort;
import org.javacord.api.DiscordApi;
import org.javacord.api.event.interaction.ButtonClickEvent;
import org.javacord.api.listener.interaction.ButtonClickListener;
import org.javacord.api.util.event.ListenerManager;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
@RequiredArgsConstructor
public class DiscordSubscriptionDeleteButtonHandler {

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

    private void handleButtonClick(ButtonClickEvent event) {

        Integer subscriptionId = parseSubscriptionId(event);

        if (subscriptionId == null) return;

        String message = deleteRideSubscriptionPort.delete(subscriptionId)
                .map(s -> "Subscription deleted")
                .orElse("Subscription was already deleted");

        event.getButtonInteraction()
                .createOriginalMessageUpdater()
                .setContent(message)
                .update();
    }

    private Integer parseSubscriptionId(ButtonClickEvent event) {

        String customId = event.getButtonInteraction().getCustomId();

        if (!customId.startsWith("delete_subscription_")) {
            return null;
        }

        String subscriptionIdString = customId.replace("delete_subscription_", "");

        return Integer.parseInt(subscriptionIdString);
    }
}
