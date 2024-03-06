package bot.awesome.bot.util;

import bot.awesome.bot.repository.DailyPickRepository;
import bot.awesome.bot.repository.UserRepository;
import bot.awesome.bot.service.DailyPickService;
import bot.awesome.bot.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
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

//    @Override
//    public void onUpdateReceived(Update update) {
//        if (update.hasCallbackQuery()) {
////            String callData = update.getCallbackQuery().getData();
//            String messageText = update.getMessage().getText();
//            String chatId = update.getMessage().getChatId().toString();
//            String userId = update.getMessage().getFrom().getId().toString();
//            String username = update.getMessage().getFrom().getUserName();
//            if (username == null || username.isEmpty()) {
//                username = update.getMessage().getFrom().getFirstName() + " " + update.getMessage().getFrom().getLastName();
//            }
//            switch (messageText) {
//                case "/reg":
//                    registerOrNotifyUser(chatId, userId, username);
//                    break;
//                case "/game":
//                    pickBeautyOfTheDay(chatId);
//                    break;
//                case "/stat":
//                    showParticipantsStats(chatId);
//                    break;
//                case "/resetStats":
//                    dailyPickService.resetStatistics(chatId);
//                    sendMessage(chatId, "Статистика успешно сброшена.");
//                    break;
//                case "/ss":
//                    // вывод всех учатников
//                    showParticipants(chatId);
//                    break;
//                case "/deleteAll":
///*
//                     TODO
//                    if (isAdmin(chatId)) { // Предполагается, что вы проверяете, является ли пользователь администратором
//*/
//                    dailyPickService.resetStatistics(chatId);
//                    userService.deleteAllUsers(chatId);
//                    sendMessage(chatId, "Все пользователи удалены.");
///*
//                    } else {
//                        sendMessage(chatId, "У вас нет прав для выполнения этой команды.");
//                    }
//*/
//                    break;
//            }
//        }
//    }
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();
            String userId = update.getMessage().getFrom().getId().toString();
            String username = update.getMessage().getFrom().getUserName();
            if (username == null || username.isEmpty()) {
                username = update.getMessage().getFrom().getFirstName() + " " + update.getMessage().getFrom().getLastName();
            }
            switch (messageText) {
                case "/reg":
                    registerOrNotifyUser(chatId, userId, username);
                    break;
                case "/game":
                    pickBeautyOfTheDay(chatId);
                    break;
                case "/stat":
                    showParticipantsStats(chatId);
                    break;
                case "/allPlayers":
                    // вывод всех учатников
                    showParticipants(chatId);
                    break;
                case "/resetStats":
                    dailyPickService.resetStatistics(chatId);
                    sendMessage(chatId, "Статистика успешно сброшена.");
                    break;
                case "/deleteAll":
/*
                     TODO
                    if (isAdmin(chatId)) { // Предполагается, что вы проверяете, является ли пользователь администратором
*/
                    dailyPickService.resetStatistics(chatId);
                    userService.deleteAllUsers(chatId);
                    sendMessage(chatId, "Все пользователи удалены.");
/*
                    } else {
                        sendMessage(chatId, "У вас нет прав для выполнения этой команды.");
                    }
*/
                    break;
            }
        }

        if (update.hasCallbackQuery()) {
            // Получаем данные из CallbackQuery
            String callbackData = update.getCallbackQuery().getData();
            String chatId = update.getCallbackQuery().getMessage().getChatId().toString();
            Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
            String userId = update.getCallbackQuery().getFrom().getId().toString();
            String username = update.getCallbackQuery().getFrom().getUserName();

            // Обработка CallbackQuery
            if ("/reg".equals(callbackData)) {
                registerOrNotifyUser(chatId, userId, username);
            } else if ("/stat".equals(callbackData)) {
                showParticipantsStats(chatId);
            } else if ("/game".equals(callbackData)) {
                pickBeautyOfTheDay(chatId);
            } else if ("/allPlayers".equals(callbackData)) {
                showParticipants(chatId);
            }

            // Удаление исходного сообщения с клавиатурой, если требуется
            try {
                execute(new DeleteMessage(chatId, messageId));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    public void showParticipants(String chatId) {
        // Предполагается, что этот метод возвращает список пользователей для данного chatId
        List<User> users = userService.getUsersByChatId(chatId);
        if (users.isEmpty()) {
            sendMessage(chatId, "В этом чате пока нет участников.");
        } else {
            StringBuilder messageText = new StringBuilder("Участники в этом чате:\n");
            for (User user : users) {
                messageText.append(user.getUsername()).append("\n");
            }
            sendMessage(chatId, messageText.toString());
        }
    }

    private void sendCommandButtons(String chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Во что сыграем?");
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        // Adding buttons
        List<InlineKeyboardButton> row1 = new ArrayList<>();

        InlineKeyboardButton reg = new InlineKeyboardButton();
        reg.setText("Регистрация ✍\uFE0F");
        reg.setCallbackData("/reg");
        row1.add(reg);

        InlineKeyboardButton stat = new InlineKeyboardButton();
        stat.setText("Статистика \uD83D\uDCC8");
        stat.setCallbackData("/stat");
        row1.add(stat);
        keyboard.add(row1);

        List<InlineKeyboardButton> row2 = new ArrayList<>();

        InlineKeyboardButton game = new InlineKeyboardButton();
        game.setText("Красавчик \uD83D\uDE0E");
        game.setCallbackData("/game");
        row2.add(game);

        InlineKeyboardButton allPlayers = new InlineKeyboardButton();
        allPlayers.setText("Участники \uD83D\uDC6B");
        allPlayers.setCallbackData("/allPlayers");
        row2.add(allPlayers);
        keyboard.add(row2);

        inlineKeyboardMarkup.setKeyboard(keyboard);
        message.setReplyMarkup(inlineKeyboardMarkup);
        try {
            execute(message); // Отправляем сообщение с клавиатурой
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void registerOrNotifyUser(String chatId, String userId, String username) {
        userService.findByChatIdAndUserId(chatId, userId).ifPresentOrElse(user -> {
            sendMessage(chatId, "Вы уже в игре.");
        }, () -> {
            User newUser = new User();
            newUser.setChatId(chatId);
            newUser.setUserId(userId);
            newUser.setUsername(username); // Set the username as well
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
                message.append(user.getUsername())
                        .append(" - Красавчик дня ")
                        .append(count)
                        .append(" раз(а)\n");
            }
        });

        sendMessage(chatId, message.toString());
    }

    private void pickBeautyOfTheDay(String chatId) {
        LocalDate today = LocalDate.now();
        List<User> users = userService.findAllUsersByChatId(chatId);
        if (users.isEmpty()) {
            sendMessage(chatId, "Пожалуйста, зарегистрируйтесь, используя команду /start.");
            return;
        }
        boolean alreadyPicked = dailyPickRepository.existsByChatIdAndPickDate(chatId, today);

        if (alreadyPicked) {
            List<DailyPick> dailyPicks = dailyPickRepository.findByChatIdAndPickDate(chatId, today);
            if (!dailyPicks.isEmpty()) {
                // Выбираем последнюю запись, если есть несколько
                DailyPick lastPick = dailyPicks.get(dailyPicks.size() - 1);
                Optional<User> user = userRepository.findById(lastPick.getUserId());
                if (user.isPresent()) {
                    String message = "Сегодня красавчика дня выбирали.\nИм стал: " + user.get().getUsername();
                    sendMessage(chatId, message);
                } else {
                    sendMessage(chatId, "Не удалось найти пользователя для красавчика дня.");
                }
            } else {
                sendMessage(chatId, "Сегодня красавчик дня не выбирался.");
            }

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
        sendCommandButtons(chatId);
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
