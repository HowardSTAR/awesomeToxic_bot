package org.example.service;

import org.example.repository.DailyPickRepository;
import org.example.util.User;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final DailyPickRepository dailyPickRepository;

    @Autowired
    public UserService(UserRepository userRepository, DailyPickRepository dailyPickRepository) {
        this.userRepository = userRepository;
        this.dailyPickRepository = dailyPickRepository;
    }

    @Transactional(readOnly = true)
    public List<User> findAllUsers() {
        return userRepository.findAll();
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

    public Optional<User> findByChatId(String chatId) {
        return userRepository.findByChatId(chatId);
    }

    public boolean isUserRegistered(String chatId) {
        return userRepository.findByChatId(chatId) != null;
    }
}
