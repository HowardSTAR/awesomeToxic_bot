package org.example.util;

import org.example.repository.DailyPickRepository;
import org.example.repository.UserRepository;
import org.example.service.DailyPickService;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class BeautyPickerBot extends TelegramLongPollingBot {
    private final UserRepository userRepository;
    private final DailyPickRepository dailyPickRepository;

    private final UserService userService;
    private final DailyPickService dailyPickService;

    @Value("${bot.username}")
    private String botUsername;

    @Value("${bot.token}")
    private String botToken;

    public BeautyPickerBot(UserRepository userRepository, DailyPickRepository dailyPickRepository,
                           UserService userService, DailyPickService dailyPickService,
                           String botUsername, String botToken) {
        this.userRepository = userRepository;
        this.dailyPickRepository = dailyPickRepository;
        this.userService = userService;
        this.dailyPickService = dailyPickService;
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
                    registerOrNotifyUser(chatId, username);
                    break;
                case "/pick":
                    pickBeautyOfTheDay(chatId);
                    break;
                case "/stats":
                    showParticipantsStats(chatId);
                    break;
                case "/resetStats":
                    dailyPickService.resetStatistics();
                    sendMessage(chatId, "Статистика успешно сброшена.");
                    break;
            }
        }
    }

    private void registerOrNotifyUser(String chatId, String userName) {
        userService.findByChatId(chatId).ifPresentOrElse(user -> {
            sendMessage(chatId, "Вы уже в игре.");
        }, () -> {
            userService.registerUser(chatId);
            sendMessage(chatId, "Отлично, у нас новенький.");
        });
    }

    private void showParticipantsStats(String chatId) {
        List<User> users = userService.findAllUsers();
        List<DailyPick> picks = dailyPickRepository.findByChatId(chatId);
        if (picks.isEmpty()) {
            sendMessage(chatId, "В этом чате еще не выбирали красавчика дня.");
            return;
        }
        if (users.isEmpty()) {
            sendMessage(chatId, "Пока нет зарегистрированных участников.");
            return;
        }

        Map<Long, Long> userIdToCount = picks.stream()
                .collect(Collectors.groupingBy(DailyPick::getUserId, Collectors.counting()));

        StringBuilder message = new StringBuilder("Статистика красавчиков дня в этом чате:\n");
        userIdToCount.forEach((userId, count) -> {
            User user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                message.append("@")
                        .append(user.getUsername())
                        .append(" - Красавчик дня ")
                        .append(count)
                        .append(" раз(а)\n");
            }
        });

        sendMessage(chatId, message.toString());
    }

    private void pickBeautyOfTheDay(String chatId) {
        LocalDate today = LocalDate.now();
        boolean alreadyPicked = dailyPickRepository.existsByChatIdAndPickDate(chatId, today);
        if (alreadyPicked) {
            sendMessage(chatId, "Красавчик дня уже был выбран сегодня.");
            return;
        }

        List<User> users = userService.findAllUsersByChatId(chatId);
        if (users.isEmpty()) {
            sendMessage(chatId, "Пожалуйста, зарегистрируйтесь, используя команду /start.");
            return;
        }

        Random rand = new Random();
        User beauty = users.get(rand.nextInt(users.size()));
        DailyPick pick = new DailyPick();
        pick.setChatId(chatId);
        pick.setUserId(beauty.getId());
        pick.setPickDate(today);
        dailyPickRepository.save(pick);

        sendMessage(chatId, "Красавчик дня: @" + beauty.getUsername());
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
