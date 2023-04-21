package io.app.arbittrading.telegram;

import io.app.arbittrading.telegram.config.TelegramConfig;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class TelegramSender {
    private final TelegramConfig telegramConfig;
    private final RestTemplate restTemplate;

    public TelegramSender(RestTemplate restTemplate, TelegramConfig telegramConfig) {
        this.restTemplate = restTemplate;
        this.telegramConfig = telegramConfig;
    }
    public void sendMessage(String message) {
        String url = String.format("https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s",
                telegramConfig.getTelegramBotToken(), telegramConfig.getTelegramChatId(), message);
        restTemplate.getForObject(url, String.class);
    }
}
