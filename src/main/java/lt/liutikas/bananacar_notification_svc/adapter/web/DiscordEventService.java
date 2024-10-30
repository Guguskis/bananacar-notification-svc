package lt.liutikas.bananacar_notification_svc.adapter.web;

import lombok.RequiredArgsConstructor;
import lt.liutikas.bananacar_notification_svc.adapter.web.model.DiscordCommandType;
import lt.liutikas.bananacar_notification_svc.application.port.in.FetchRideSubscriptionsPort;
import lt.liutikas.bananacar_notification_svc.domain.RideSubscription;
import org.javacord.api.DiscordApi;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.*;
import org.javacord.api.interaction.callback.InteractionImmediateResponseBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Set;

import static lt.liutikas.bananacar_notification_svc.adapter.web.model.DiscordCommandType.*;

@Service
@RequiredArgsConstructor
public class DiscordEventService {

    private final DiscordApi discordApi;
    private final FetchRideSubscriptionsPort fetchRideSubscriptionsPort;

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
                    .setContent("# There are no ride subscriptions, maybe create one?")
                    .respond();
            return;
        }

        InteractionImmediateResponseBuilder responseBuilder = interaction.createImmediateResponder()
                .setContent("# Listing All Ride Subscriptions")
                .appendNewLine()
                .appendNewLine();

        subscriptions.stream()
                .map(DiscordEventService::toSubscriptionLine)
                .forEach(line -> responseBuilder.append(line).appendNewLine().appendNewLine());

        responseBuilder.respond();
    }

    private void handleCreateRideSubscription(SlashCommandInteraction interaction) {

        // todo
    }

    private void handleDeleteRideSubscription(SlashCommandInteraction interaction) {

        // todo
    }

    private static String toSubscriptionLine(RideSubscription sub) {

        return String.format("> **ID**: `%s`\n> **From**: %s\n> **To**: %s",
                sub.getId(), sub.getOriginCity(), sub.getDestinationCity());
    }

    private SlashCommandBuilder getRidesSubscriptionsCommandBuilder() {

        return SlashCommand.with("rides", "Manage ride subscriptions",
                List.of(
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND_GROUP, "subscriptions", "Subscription-related commands",
                                List.of(
                                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, LIST.getLastSubCommand(), "Get all subscriptions"),
                                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, CREATE.getLastSubCommand(), "Create a new subscription",
                                                List.of(
                                                        SlashCommandOption.create(SlashCommandOptionType.STRING, "origin-city", "Origin city (e.g., Vilnius)", true),
                                                        SlashCommandOption.create(SlashCommandOptionType.STRING, "destination-city", "Destination city (e.g., Klaipeda)", true)
//                                                        SlashCommandOption.create(SlashCommandOptionType.STRING, "earliest", "Earliest departure time (yyyy-MM-dd HH:mm)", false),
//                                                        SlashCommandOption.create(SlashCommandOptionType.STRING, "latest", "Latest departure time (yyyy-MM-dd HH:mm)", false)
                                                )),

                                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, DELETE.getLastSubCommand(), "Delete a subscription by ID",
                                                List.of(
                                                        SlashCommandOption.create(SlashCommandOptionType.LONG, "id", "Subscription ID", true)
                                                ))
                                )
                        )
                ));
    }
}
