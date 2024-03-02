package org.example;

import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class BeautyPickerBot extends TelegramLongPollingBot {

    private final UserService userService;

    @Value("${bot.username}")
    private String botUsername;

    @Value("${bot.token}")
    private String botToken;

    public BeautyPickerBot(UserService userService, String botUsername, String botToken) {
        this.userService = userService;
        this.botUsername = botUsername;
        this.botToken = botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();
            String username = update.getMessage().getFrom().getUserName();
            if (username == null || username.isEmpty()) {
                username = update.getMessage().getFrom().getFirstName() + " " + update.getMessage().getFrom().getLastName();
            }
            switch (messageText) {
                case "/start":
                    userService.registerUser(chatId, username);
                    sendMessage(chatId, "Вы успешно зарегистрированы!");
                    break;
                case "/pick":
                    pickBeautyOfTheDay(chatId);
                    break;
                case "/stats":
                    showParticipantsStats(chatId);
                    break;
            }
        }
    }

    private void showParticipantsStats(String chatId) {
        List<User> users = userService.findAllUsers();
        if (users.isEmpty()) {
            sendMessage(chatId, "Пока нет зарегистрированных участников.");
            return;
        }

        StringBuilder message = new StringBuilder("Статистика участников:\n");
        for (User user : users) {
            message.append("@")
                    .append(user.getUsername() != null ? user.getUsername() : "аноним")
                    .append(" - Красавчик дня ")
                    .append(user.getBeautyCount())
                    .append(" раз(а)\n");
        }

        sendMessage(chatId, message.toString());
    }

    private void pickBeautyOfTheDay(String chatId) {
        Optional<User> optionalUser = userService.findByChatId(chatId);
        if (!optionalUser.isPresent()) {
            sendMessage(chatId, "Пожалуйста, зарегистрируйтесь, используя команду /start.");
            return;
        }

        List<User> users = userService.findAllUsers();
        if (users.isEmpty()) {
            sendMessage(chatId, "В чате пока нет зарегистрированных пользователей.");
            return;
        }

        Random random = new Random();
        int index = random.nextInt(users.size());
        User beauty = users.get(index);
        if (beauty.getUsername() == null) {
            sendMessage(chatId, "К сожалению, у красавчика дня нет никнейма в Telegram.");
        } else {
            sendMessage(chatId, "Красавчик дня: @" + beauty.getUsername());
        }
        userService.incrementBeautyCount(beauty.getChatId());
    }

    private void sendMessage(String chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
