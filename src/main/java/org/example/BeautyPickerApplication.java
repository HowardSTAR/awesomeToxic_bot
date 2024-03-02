package org.example;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class BeautyPickerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BeautyPickerApplication.class, args);
    }

    @Bean
    public TelegramBotsApi telegramBotsApi() throws Exception {
        return new TelegramBotsApi(DefaultBotSession.class);
    }

    @Bean
    public BeautyPickerBot beautyPickerBot(UserRepository userRepository,
                                           DailyPickRepository dailyPickRepository,
                                           UserService userService,
                                           @Value("${bot.username}") String botUsername,
                                           @Value("${bot.token}") String botToken) {
        BeautyPickerBot bot = new BeautyPickerBot(userRepository, dailyPickRepository, userService, botUsername, botToken);
        try {
            telegramBotsApi().registerBot(bot);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bot;
    }
}

