package lt.liutikas.bananacar_notification_svc.adapter.web.discord;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lt.liutikas.bananacar_notification_svc.adapter.web.discord.model.DiscordCommandType;
import lt.liutikas.bananacar_notification_svc.adapter.web.discord.model.DiscordInputException;
import lt.liutikas.bananacar_notification_svc.application.port.in.DeleteRideSubscriptionPort;
import lt.liutikas.bananacar_notification_svc.application.port.in.FetchRideSubscriptionsPort;
import lt.liutikas.bananacar_notification_svc.application.port.in.SaveUniqueRideSubscriptionPort;
import lt.liutikas.bananacar_notification_svc.application.port.out.RespondSubscriptionCreatedPort;
import lt.liutikas.bananacar_notification_svc.application.port.out.RespondSubscriptionDeletedPort;
import lt.liutikas.bananacar_notification_svc.application.port.out.RespondSubscriptionListPort;
import lt.liutikas.bananacar_notification_svc.application.port.out.RespondSubscriptionNotFoundPort;
import lt.liutikas.bananacar_notification_svc.common.util.Loggable;
import lt.liutikas.bananacar_notification_svc.domain.RideSubscription;
import org.javacord.api.DiscordApi;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.*;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;
import org.javacord.api.util.event.ListenerManager;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static lt.liutikas.bananacar_notification_svc.adapter.web.discord.model.DiscordCommandType.CREATE;
import static lt.liutikas.bananacar_notification_svc.adapter.web.discord.model.DiscordCommandType.LIST;

@Service
@RequiredArgsConstructor
public class DiscordEventService implements Loggable {

    private static final LocalDateTime LOCAL_DATE_TIME_MIN = LocalDateTime.of(2000, 1, 1, 1, 1);
    private static final LocalDateTime LOCAL_DATE_TIME_MAX = LocalDateTime.of(3000, 1, 1, 1, 1);
    private static final String OPTION_NAME_FROM = "from";
    private static final String OPTION_NAME_TO = "to";
    private static final String OPTION_NAME_ID = "id";

    private final DiscordApi discordApi;
    private final FetchRideSubscriptionsPort fetchRideSubscriptionsPort;
    private final SaveUniqueRideSubscriptionPort saveUniqueRideSubscriptionPort;
    private final DeleteRideSubscriptionPort deleteRideSubscriptionPort;
    private final RespondSubscriptionListPort respondSubscriptionListPort;
    private final RespondSubscriptionCreatedPort respondSubscriptionCreatedPort;
    private final RespondSubscriptionDeletedPort respondSubscriptionDeletedPortPort;
    private final RespondSubscriptionNotFoundPort respondSubscriptionNotFoundPort;

    private ListenerManager<SlashCommandCreateListener> listenerManager;

    @PostConstruct
    public void addDiscordListener() {

        SlashCommandBuilder command = getRidesSubscriptionsCommandBuilder();

        discordApi.bulkOverwriteGlobalApplicationCommands(Set.of(command)).join();
        listenerManager = discordApi.addSlashCommandCreateListener(this::processMessage);
    }

    @PreDestroy
    public void removeDiscordListener() {

        listenerManager.remove();
    }

    private void processMessage(SlashCommandCreateEvent event) {

        SlashCommandInteraction interaction = event.getSlashCommandInteraction();

        switch (DiscordCommandType.of(interaction.getFullCommandName())) {

            case LIST -> handleListRideSubscriptions(interaction);
            case CREATE -> handleCreateRideSubscription(interaction);
            case DELETE -> handleDeleteRideSubscription(interaction);
        }
    }

    private void handleListRideSubscriptions(SlashCommandInteraction interaction) {

        List<RideSubscription> subscriptions = fetchRideSubscriptionsPort.fetch();

        respondSubscriptionListPort.respondList(interaction, subscriptions);
    }

    private void handleCreateRideSubscription(SlashCommandInteraction interaction) {

        try {

            String originCity = interaction
                    .getArgumentStringRepresentationValueByName(OPTION_NAME_FROM)
                    .orElseThrow(() -> new DiscordInputException("Missing argument [%s]".formatted(OPTION_NAME_FROM)));

            String destinationCity = interaction
                    .getArgumentStringRepresentationValueByName(OPTION_NAME_TO)
                    .orElseThrow(() -> new DiscordInputException("Null option [%s]".formatted(OPTION_NAME_TO)));

            RideSubscription rideSubscription = saveUniqueRideSubscriptionPort.create(SaveUniqueRideSubscriptionPort.CreateCommand.builder()
                    .originCity(originCity)
                    .destinationCity(destinationCity)
                    .departsOnEarliest(LOCAL_DATE_TIME_MIN)
                    .departsOnLatest(LOCAL_DATE_TIME_MAX)
                    .build());

            respondSubscriptionCreatedPort.respondCreated(interaction, rideSubscription);

        } catch (DiscordInputException e) {

            interaction.createImmediateResponder()
                    .setContent(e.getMessage())
                    .respond();
        }
    }

    private void handleDeleteRideSubscription(SlashCommandInteraction interaction) {

        try {

            Long subscriptionId = interaction
                    .getArgumentLongValueByName(OPTION_NAME_ID)
                    .orElseThrow(() -> new DiscordInputException("Missing argument [%s]".formatted(OPTION_NAME_ID)));

            getLogger().info("Deleting ride subscription id [{}]", subscriptionId);

            deleteRideSubscriptionPort.delete(subscriptionId.intValue())
                    .ifPresentOrElse(
                            subscription -> respondSubscriptionDeletedPortPort.respondDeleted(interaction, subscription),
                            () -> respondSubscriptionNotFoundPort.respondDeletedNotFound(interaction)
                    );

        } catch (DiscordInputException e) {

            interaction.createImmediateResponder()
                    .setContent("Error: %s".formatted(e.getMessage()))
                    .respond();
        }
    }

    private SlashCommandBuilder getRidesSubscriptionsCommandBuilder() {

        return SlashCommand.with("rides", "Manage ride subscriptions",
                List.of(
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND_GROUP, "subscriptions", "Subscription-related commands",
                                List.of(
                                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, LIST.getLastSubCommand(), "Get all subscriptions"),
                                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, CREATE.getLastSubCommand(), "Create a new subscription",
                                                List.of(
                                                        SlashCommandOption.create(SlashCommandOptionType.STRING, OPTION_NAME_FROM, "Origin city (e.g., Vilnius)", true),
                                                        SlashCommandOption.create(SlashCommandOptionType.STRING, OPTION_NAME_TO, "Destination city (e.g., Klaipeda)", true)
//                                                        SlashCommandOption.create(SlashCommandOptionType.STRING, "earliest", "Earliest departure time (yyyy-MM-dd HH:mm)", false),
//                                                        SlashCommandOption.create(SlashCommandOptionType.STRING, "latest", "Latest departure time (yyyy-MM-dd HH:mm)", false)
                                                ))
                                )
                        )
                ));
    }
}
