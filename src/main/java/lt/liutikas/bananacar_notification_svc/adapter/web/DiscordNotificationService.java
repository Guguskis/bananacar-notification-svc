package lt.liutikas.bananacar_notification_svc.adapter.web;

import lombok.RequiredArgsConstructor;
import lt.liutikas.bananacar_notification_svc.application.port.out.NotificationPort;
import lt.liutikas.bananacar_notification_svc.common.Loggable;
import lt.liutikas.bananacar_notification_svc.domain.Ride;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class DiscordNotificationService implements Loggable, NotificationPort {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final ServerTextChannel channel;

    @Override
    public void notify(Ride ride) {


        String message = "> **Departure**: `%s`\n> **From**: %s\n> **To**: %s"
                .formatted(
                        ride.getDepartsOn().format(DATE_TIME_FORMATTER),
                        ride.getOrigin().getCity(),
                        ride.getDestination().getCity()
                );

        getLogger().info("Sending ride notification");

        new MessageBuilder()
                .setContent("## New ride appeared")
                .appendNewLine()
                .append(message)
                .addComponents(linkButton(ride.getBananacarUrl()))
                .send(channel);
    }

    private ActionRow linkButton(URL url) {

        return ActionRow.of(
                Button.link(url.toString(), "BananaCar")
        );
    }
}
