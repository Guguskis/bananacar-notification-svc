package lt.liutikas.bananacar_notification_svc.adapter.web.discord;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lt.liutikas.bananacar_notification_svc.adapter.web.discord.model.DiscordCommandType;
import lt.liutikas.bananacar_notification_svc.adapter.web.discord.model.DiscordInputException;
import lt.liutikas.bananacar_notification_svc.application.port.in.DeleteRideSubscriptionPort;
import lt.liutikas.bananacar_notification_svc.application.port.in.FetchRideSubscriptionsPort;
import lt.liutikas.bananacar_notification_svc.application.port.in.SaveUniqueRideSubscriptionPort;
import lt.liutikas.bananacar_notification_svc.common.util.Loggable;
import lt.liutikas.bananacar_notification_svc.domain.RideSubscription;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.event.interaction.ButtonClickEvent;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.*;
import org.javacord.api.listener.interaction.ButtonClickListener;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;
import org.javacord.api.util.event.ListenerManager;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static lt.liutikas.bananacar_notification_svc.adapter.web.discord.DiscordMessageFormatter.*;
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
    private static final String DELETE_SUBSCRIPTION_PREFIX = "delete_subscription_";
    private static final String MESSAGE_SUBSCRIPTION_DELETED = "Subscription deleted";
    private static final String MESSAGE_SUBSCRIPTION_ALREADY_DELETED = "Subscription was already deleted";

    private final DiscordApi discordApi;
    private final FetchRideSubscriptionsPort fetchRideSubscriptionsPort;
    private final SaveUniqueRideSubscriptionPort saveUniqueRideSubscriptionPort;
    private final DeleteRideSubscriptionPort deleteRideSubscriptionPort;

    private ListenerManager<SlashCommandCreateListener> slashCommandListenerManager;
    private ListenerManager<ButtonClickListener> buttonClickListenerListenerManager;

    @PostConstruct
    public void setup() {

        SlashCommandBuilder command = getRidesSubscriptionsCommandBuilder();

        discordApi.bulkOverwriteGlobalApplicationCommands(Set.of(command)).join();
        slashCommandListenerManager = discordApi.addSlashCommandCreateListener(this::handleSlashCommand);
        buttonClickListenerListenerManager = discordApi.addButtonClickListener(this::handleButtonClick);
    }

    @PreDestroy
    public void cleanup() {

        slashCommandListenerManager.remove();
        buttonClickListenerListenerManager.remove();
    }

    private void handleSlashCommand(SlashCommandCreateEvent event) {

        SlashCommandInteraction interaction = event.getSlashCommandInteraction();

        switch (DiscordCommandType.of(interaction.getFullCommandName())) {

            case LIST -> handleListRideSubscriptions(interaction);
            case CREATE -> handleCreateRideSubscription(interaction);
            case DELETE -> handleDeleteRideSubscription(interaction);
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


    private void handleListRideSubscriptions(SlashCommandInteraction interaction) {

        List<RideSubscription> subscriptions = fetchRideSubscriptionsPort.fetch();

        if (subscriptions.isEmpty()) {
            respondListSubscriptionsEmpty(interaction);
        } else {
            respondListSubscriptions(interaction, subscriptions);
        }
    }

    private void handleCreateRideSubscription(SlashCommandInteraction interaction) {

        try {

            String originCity = interaction
                    .getArgumentStringRepresentationValueByName(OPTION_NAME_FROM)
                    .orElseThrow(() -> new DiscordInputException("Missing argument [%s]".formatted(OPTION_NAME_FROM)));

            String destinationCity = interaction
                    .getArgumentStringRepresentationValueByName(OPTION_NAME_TO)
                    .orElseThrow(() -> new DiscordInputException("Null option [%s]".formatted(OPTION_NAME_TO)));

            RideSubscription subscription = saveUniqueRideSubscriptionPort.create(SaveUniqueRideSubscriptionPort.CreateCommand.builder()
                    .originCity(originCity)
                    .destinationCity(destinationCity)
                    .departsOnEarliest(LOCAL_DATE_TIME_MIN)
                    .departsOnLatest(LOCAL_DATE_TIME_MAX)
                    .build());

            respondCreated(interaction, subscription);

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
                            sub -> respondDeleted(interaction, sub),
                            () -> respondDeletedNotFound(interaction)
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

    private void respondListSubscriptionsEmpty(SlashCommandInteraction interaction) {

        respond(
                interaction,
                toPlainSubscriptionsListEmptyMessage(),
                toDecoratedSubscriptionsListEmptyMessage()
        );
    }

    private void respondListSubscriptions(SlashCommandInteraction interaction, List<RideSubscription> subscriptions) {

        respond(
                interaction,
                toPlainSubscriptionsListMessage(),
                toDecoratedSubscriptionsListMessage()
        );

        TextChannel channel = interaction.getChannel()
                .orElseThrow(() -> new IllegalStateException("Failed to acquire discord channel"));

        subscriptions.forEach(subscription -> sendSubscriptionMessage(channel, subscription));
    }

    public void respondCreated(SlashCommandInteraction interaction, RideSubscription subscription) {

        respond(
                interaction,
                toPlainSubscriptionCreatedMessage(),
                toDecoratedSubscriptionCreatedMessage()
        );

        TextChannel channel = interaction.getChannel()
                .orElseThrow(() -> new IllegalStateException("Failed to acquire discord channel"));

        sendSubscriptionMessage(channel, subscription);
    }

    public void respondDeleted(SlashCommandInteraction interaction, RideSubscription subscription) {

        respond(
                interaction,
                toPlainSubscriptionDeletedMessage(subscription),
                toDecoratedSubscriptionDeletedMessage(subscription)
        );
    }

    public void respondDeletedNotFound(SlashCommandInteraction interaction) {

        respond(
                interaction,
                toPlainSubscriptionDeletedMessage(),
                toDecoratedSubscriptionDeletedMessage()
        );
    }

    private void sendSubscriptionMessage(TextChannel channel, RideSubscription subscription) {

        String plainMessage = toPlain(subscription);
        String decoratedMessage = toDecorated(subscription);
        ActionRow deleteButton = createDeleteButton(subscription);

        new MessageBuilder()
                .setContent(plainMessage)
                .addComponents(deleteButton)
                .send(channel)
                .thenAcceptAsync(message -> message.edit(decoratedMessage));
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

    private void respond(SlashCommandInteraction interaction, String pushNotificationContent, String messageContent) {

        interaction.createImmediateResponder()
                .setContent(pushNotificationContent)
                .respond()
                .thenAcceptAsync(action -> action
                        .setContent(messageContent)
                        .update()
                        .join())
                .join();
    }
}
