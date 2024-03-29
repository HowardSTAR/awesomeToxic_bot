package bot.awesome.bot.service;

import bot.awesome.bot.repository.DailyPickRepository;
import bot.awesome.bot.util.User;
import bot.awesome.bot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final DailyPickRepository dailyPickRepository;
    private final DailyPickService dailyPickService;

    @Autowired
    public UserService(UserRepository userRepository,
                       DailyPickRepository dailyPickRepository,
                       DailyPickService dailyPickService) {
        this.userRepository = userRepository;
        this.dailyPickRepository = dailyPickRepository;
        this.dailyPickService = dailyPickService;
    }

    @Transactional(readOnly = true)
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public void deleteAllUsers(String chatId) {
        userRepository.deleteByChatId(chatId);
    }

    public List<User> getUsersByChatId(String chatId) {
        return userRepository.findAllByChatId(chatId);
    }

    public void registerUser(String chatId) {
        userRepository.findByChatId(chatId).ifPresentOrElse(user -> {
            // Пользователь найден, можно отправить сообщение о том, что он уже зарегистрирован
        }, () -> {
            // Пользователь не найден, регистрируем нового
            User newUser = new User(chatId);
            userRepository.save(newUser);
        });
    }

    public List<User> findAllUsersByChatId(String chatId) {
        return userRepository.findAllByChatId(chatId);
    }

    public Optional<User> findByChatIdAndUserId(String chatId, String userId) {
        return userRepository.findByChatIdAndUserId(chatId, userId);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User save(User user) {
        return userRepository.save(user);
    }
}
