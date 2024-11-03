package lt.liutikas.bananacar_notification_svc.application.port.out;

import lt.liutikas.bananacar_notification_svc.domain.RideSubscription;
import org.javacord.api.interaction.SlashCommandInteraction;

import java.util.List;

public interface RespondListSubscriptionsPort {

    void respondList(SlashCommandInteraction interaction, List<RideSubscription> subscriptions);
}
