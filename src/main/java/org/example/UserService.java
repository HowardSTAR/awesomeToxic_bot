package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public void incrementBeautyCount(String chatId) {
        Optional<User> optionalUser = userRepository.findByChatId(chatId);
        User user = optionalUser.orElseGet(() -> new User(chatId));
        user.setBeautyCount(user.getBeautyCount() + 1);
        userRepository.save(user);
    }

    public void registerUser(String chatId, String username) {
        Optional<User> optionalUser = userRepository.findByChatId(chatId);
        if (!optionalUser.isPresent()) {
            User newUser = new User();
            newUser.setChatId(chatId);
            newUser.setUsername(username);
            userRepository.save(newUser);
        }
    }

    public Optional<User> findByChatId(String chatId) {
        return userRepository.findByChatId(chatId);
    }

    public boolean isUserRegistered(String chatId) {
        return userRepository.findByChatId(chatId) != null;
    }
}
