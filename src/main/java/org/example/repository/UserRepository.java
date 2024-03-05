package org.example.repository;

import org.example.util.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByChatId(String chatId);
    List<User> findAllByChatId(String userName);
    Optional<User> findByUsername(String username);
}
