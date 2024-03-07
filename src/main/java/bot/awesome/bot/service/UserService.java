package bot.awesome.bot.service;

import bot.awesome.bot.repository.DailyPickRepository;
import bot.awesome.bot.repository.UserRepository;
import bot.awesome.bot.util.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

// Класс сервиса для управления пользователями в системе.
@Service
public class UserService {
    private final UserRepository userRepository;
    // В текущей версии кода не используется, можно рассмотреть возможность удаления
    private final DailyPickRepository dailyPickRepository;
    // Также не используется в представленном коде
    private final DailyPickService dailyPickService;

    // Конструктор для автоматической инъекции зависимостей.
    @Autowired
    public UserService(UserRepository userRepository,
                       DailyPickRepository dailyPickRepository,
                       DailyPickService dailyPickService) {
        this.userRepository = userRepository;
        this.dailyPickRepository = dailyPickRepository;
        this.dailyPickService = dailyPickService;
    }

    // Возвращает список всех пользователей из базы данных.
    @Transactional(readOnly = true)
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    // Удаляет всех пользователей в указанном чате.
    @Transactional
    public void deleteAllUsers(String chatId) {
        userRepository.deleteByChatId(chatId);
    }

    // Возвращает список пользователей по идентификатору чата.
    public List<User> getUsersByChatId(String chatId) {
        return userRepository.findAllByChatId(chatId);
    }

    // Регистрация пользователя в системе по идентификатору чата.
    public void registerUser(String chatId) {
        userRepository.findByChatId(chatId).ifPresentOrElse(user -> {
            // Если пользователь уже зарегистрирован, можно реализовать логику оповещения.
        }, () -> {
            // Регистрация нового пользователя, если он ещё не зарегистрирован.
            User newUser = new User(chatId);
            userRepository.save(newUser);
        });
    }

    // Возвращает всех пользователей по идентификатору чата.
    public List<User> findAllUsersByChatId(String chatId) {
        return userRepository.findAllByChatId(chatId);
    }

    // Находит пользователя по идентификатору чата и ID пользователя.
    public Optional<User> findByChatIdAndUserId(String chatId, String userId) {
        return userRepository.findByChatIdAndUserId(chatId, userId);
    }

    // Находит пользователя по его username.
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Сохраняет пользователя в базе данных и возвращает сохранённый объект.
    public User save(User user) {
        return userRepository.save(user);
    }
}
