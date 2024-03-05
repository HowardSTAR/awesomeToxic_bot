package org.example.util;

import org.example.repository.DailyPickRepository;
import org.example.repository.UserRepository;
import org.example.service.DailyPickService;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.util.*;
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
                    pickBeautyOfTheDay(chatId, username);
                    break;
                case "/stats":
                    showParticipantsStats(chatId);
                    break;
                case "/resetStats":
                    dailyPickService.resetStatistics();
                    sendMessage(chatId, "Статистика успешно сброшена.");
                    break;
                case "/deleteAll":
/*
                     TODO
                    if (isAdmin(chatId)) { // Предполагается, что вы проверяете, является ли пользователь администратором
*/
                        userService.deleteAllUsers();
                        sendMessage(chatId, "Все пользователи удалены.");
/*
                    } else {
                        sendMessage(chatId, "У вас нет прав для выполнения этой команды.");
                    }
*/
                    break;
            }
        }
    }

    private void sendCommandButtons() {
        // Создаем клавиатуру
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        // Добавляем кнопки
        KeyboardRow row = new KeyboardRow();
        row.add("/start");
        row.add("/pick");
        // Добавляем строку кнопок в клавиатуру
        keyboard.add(row);

        // Добавляем еще кнопки по необходимости
        row = new KeyboardRow();
        row.add("/stats");
        row.add("/resetStats");
        keyboard.add(row);

        // Настройки клавиатуры
        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true); // Сделать клавиатуру одноразовой, если нужно
    }

    private void registerOrNotifyUser(String chatId, String username) {
        userService.findByUsername(username).ifPresentOrElse(user -> {
            sendMessage(chatId, "Вы уже в игре.");
        }, () -> {
            User newUser = new User();
            newUser.setChatId(chatId); // Сохраняем chatId для связи с пользователем в Telegram
            newUser.setUsername(username); // Используем username как уникальный идентификатор
            userService.save(newUser);
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

    private void pickBeautyOfTheDay(String chatId, String username) {
        LocalDate today = LocalDate.now();
        List<User> users = userService.findAllUsersByChatId(chatId);
        if (users.isEmpty()) {
            sendMessage(username, "Пожалуйста, зарегистрируйтесь, используя команду /start.");
            return;
        }
        Random rand = new Random();
        User beauty = users.get(rand.nextInt(users.size()));
        boolean alreadyPicked = dailyPickRepository.existsByChatIdAndPickDate(chatId, today);

        if (alreadyPicked) {
            sendMessage(chatId, "Красавчик дня уже был выбран сегодня.\nЭто у нас: ");
            return;
        }

        DailyPick pick = new DailyPick();
        pick.setChatId(chatId);
        pick.setUserId(beauty.getId());
        pick.setPickDate(today);
        dailyPickRepository.save(pick);

        sendMessage(chatId, "Красавчик дня: @" + beauty.getUsername());
    }

    private void sendMessage(String chatId, String text) {
        sendCommandButtons();
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    // TODO
    private boolean isAdmin(String userId) {
        // Предположим, что у вас есть список или массив ID администраторов
        List<String> adminIds = Arrays.asList("12345", "67890"); // Пример ID администраторов
        return adminIds.contains(userId);
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
