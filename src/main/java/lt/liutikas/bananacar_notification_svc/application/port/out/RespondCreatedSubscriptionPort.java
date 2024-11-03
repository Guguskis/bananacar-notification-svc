package lt.liutikas.bananacar_notification_svc.application.port.out;

import lt.liutikas.bananacar_notification_svc.domain.RideSubscription;
import org.javacord.api.interaction.SlashCommandInteraction;

public interface RespondCreatedSubscriptionPort {

    void respondCreated(SlashCommandInteraction interaction, RideSubscription rideSubscription);
}
