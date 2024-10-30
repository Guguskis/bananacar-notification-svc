package lt.liutikas.bananacar_notification_svc.adapter.web;

import lombok.RequiredArgsConstructor;
import lt.liutikas.bananacar_notification_svc.application.port.out.NotificationPort;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.springframework.stereotype.Service;

import java.net.URL;

@Service
@RequiredArgsConstructor
public class DiscordNotificationService implements NotificationPort {

    private final ServerTextChannel channel;

    @Override
    public void notify(String message, URL bananacarUrl) {

        new MessageBuilder()
                .addComponents(linkButton(bananacarUrl))
                .append(message)
                .send(channel);
    }

    private ActionRow linkButton(URL url) {

        return ActionRow.of(
                Button.link(url.toString(), "BananaCar")
        );
    }
}
