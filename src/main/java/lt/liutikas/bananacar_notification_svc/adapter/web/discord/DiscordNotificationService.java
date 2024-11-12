package lt.liutikas.bananacar_notification_svc.adapter.web.discord;

import lombok.RequiredArgsConstructor;
import lt.liutikas.bananacar_notification_svc.application.port.out.NotifyNewRidePort;
import lt.liutikas.bananacar_notification_svc.common.util.Loggable;
import lt.liutikas.bananacar_notification_svc.domain.Ride;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.springframework.stereotype.Service;

import java.net.URL;

import static lt.liutikas.bananacar_notification_svc.adapter.web.discord.DiscordMessageFormatter.toDecoratedRideCreatedMessage;
import static lt.liutikas.bananacar_notification_svc.adapter.web.discord.DiscordMessageFormatter.toPlainRideCreatedMessage;

@Service
@RequiredArgsConstructor
public class DiscordNotificationService implements Loggable, NotifyNewRidePort {

    private final ServerTextChannel channel;

    @Override
    public void notify(Ride ride) {

        getLogger().info("Sending new ride notification");

        new MessageBuilder()
                .addComponents(linkButton(ride.getBananacarUrl()))
                .setContent(toPlainRideCreatedMessage(ride))
                .send(channel)
                .thenAcceptAsync(action -> action
                        .edit(toDecoratedRideCreatedMessage(ride))
                        .join()
                )
                .join();
    }

    private static ActionRow linkButton(URL url) {

        return ActionRow.of(
                Button.link(url.toString(), "BananaCar")
        );
    }

}
