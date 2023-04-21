package io.app.arbittrading.telegram.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@Data
public class TelegramConfig {
    @Value("${telegram.bot.token}")
    protected String telegramBotToken;

    @Value("${telegram.chat.id}")
    protected String telegramChatId;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
