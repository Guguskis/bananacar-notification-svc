package lt.liutikas.bananacar_notification_svc.adapter.web;

import lombok.RequiredArgsConstructor;
import lt.liutikas.bananacar_notification_svc.adapter.web.model.DiscordCommandType;
import lt.liutikas.bananacar_notification_svc.adapter.web.model.DiscordInputException;
import lt.liutikas.bananacar_notification_svc.application.port.in.DeleteRideSubscriptionPort;
import lt.liutikas.bananacar_notification_svc.application.port.in.FetchRideSubscriptionsPort;
import lt.liutikas.bananacar_notification_svc.application.port.in.SaveRideSubscriptionPort;
import lt.liutikas.bananacar_notification_svc.common.Loggable;
import lt.liutikas.bananacar_notification_svc.domain.RideSubscription;
import org.javacord.api.DiscordApi;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.*;
import org.javacord.api.interaction.callback.InteractionImmediateResponseBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static lt.liutikas.bananacar_notification_svc.adapter.web.model.DiscordCommandType.*;

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
    private final SaveRideSubscriptionPort saveRideSubscriptionPort;
    private final DeleteRideSubscriptionPort deleteRideSubscriptionPort;

    @PostConstruct
    public void addDiscordListener() {

        SlashCommandBuilder command = getRidesSubscriptionsCommandBuilder();

        discordApi.bulkOverwriteGlobalApplicationCommands(Set.of(command)).join();
        discordApi.addSlashCommandCreateListener(this::processMessage);
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

        if (subscriptions.isEmpty()) {

            interaction.createImmediateResponder()
                    .setContent("## There are no ride subscriptions, maybe create one?")
                    .respond();
            return;
        }

        InteractionImmediateResponseBuilder responseBuilder = interaction.createImmediateResponder()
                .setContent("## Listing All Ride Subscriptions")
                .appendNewLine()
                .appendNewLine();

        subscriptions.stream()
                .map(DiscordEventService::toSubscriptionLine)
                .forEach(line -> responseBuilder.append(line).appendNewLine().appendNewLine());

        responseBuilder.respond();
    }

    private void handleCreateRideSubscription(SlashCommandInteraction interaction) {

        try {

            String originCity = interaction
                    .getArgumentStringRepresentationValueByName(OPTION_NAME_FROM)
                    .orElseThrow(() -> new DiscordInputException("Missing argument [%s]".formatted(OPTION_NAME_FROM)));

            String destinationCity = interaction
                    .getArgumentStringRepresentationValueByName(OPTION_NAME_TO)
                    .orElseThrow(() -> new DiscordInputException("Null option [%s]".formatted(OPTION_NAME_TO)));

            RideSubscription rideSubscription = saveRideSubscriptionPort.create(SaveRideSubscriptionPort.CreateCommand.builder()
                    .originCity(originCity)
                    .destinationCity(destinationCity)
                    .departsOnEarliest(LOCAL_DATE_TIME_MIN)
                    .departsOnLatest(LOCAL_DATE_TIME_MAX)
                    .build());

            interaction.createImmediateResponder()
                    .setContent("## Created ride subscription")
                    .appendNewLine()
                    .append(toSubscriptionLine(rideSubscription))
                    .respond();

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

            deleteRideSubscriptionPort.delete(subscriptionId.intValue());

            interaction.createImmediateResponder()
                    .setContent("## Deleted ride subscription")
                    .respond();

        } catch (DiscordInputException e) {

            interaction.createImmediateResponder()
                    .setContent(e.getMessage())
                    .respond();
        }
    }

    private static String toSubscriptionLine(RideSubscription subscription) {

        return "> **ID**: `%s`\n> **From**: %s\n> **To**: %s"
                .formatted(
                        subscription.getId(),
                        subscription.getOriginCity(),
                        subscription.getDestinationCity()
                );
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
                                                )),

                                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, DELETE.getLastSubCommand(), "Delete a subscription by ID",
                                                List.of(
                                                        SlashCommandOption.create(SlashCommandOptionType.LONG, OPTION_NAME_ID, "Subscription ID", true)
                                                ))
                                )
                        )
                ));
    }
}
