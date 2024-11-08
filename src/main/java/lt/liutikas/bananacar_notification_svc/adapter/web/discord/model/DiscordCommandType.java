package lt.liutikas.bananacar_notification_svc.adapter.web.discord.model;

import java.util.Arrays;

public enum DiscordCommandType {

    LIST("rides subscriptions list"),
    CREATE("rides subscriptions create"),
    DELETE("rides subscriptions delete");

    private final String discordFullCommandName;

    DiscordCommandType(String discordFullCommandName) {

        this.discordFullCommandName = discordFullCommandName;
    }

    public String getLastSubCommand() {

        String[] parts = discordFullCommandName.split(" ");
        return parts.length > 0 ? parts[parts.length - 1] : "";
    }

    public static DiscordCommandType of(String discordFullCommandName) {

        return Arrays.stream(values())
                .filter(commandType -> commandType.discordFullCommandName.equalsIgnoreCase(discordFullCommandName))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Unknown discord command [%s]"
                                        .formatted(discordFullCommandName)));
    }
}
