package lt.liutikas.bananacar_notification_svc.application.port.out;

import org.javacord.api.interaction.SlashCommandInteraction;

public interface RespondSubscriptionNotFoundPort {

    void respondDeletedNotFound(SlashCommandInteraction interaction);
}
