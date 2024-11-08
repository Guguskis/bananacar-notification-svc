package lt.liutikas.bananacar_notification_svc.adapter.web.discord;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.intent.Intent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DiscordConfiguration {

    @Value("${discord.channelId}")
    private String channelId;
    @Value("${discord.token}")
    private String discordToken;

    @Bean
    public DiscordApi discordApi() {

        return new DiscordApiBuilder()
                .setToken(discordToken)
                .addIntents(Intent.MESSAGE_CONTENT)
                .login()
                .join();
    }

    @Bean
    public ServerTextChannel discordChannel(DiscordApi discordApi) {

        return discordApi.getServerTextChannelById(channelId)
                .orElseThrow(() -> new IllegalStateException("Unable to acquire discord channel"));
    }
}
