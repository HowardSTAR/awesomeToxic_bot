package bot.awesome.bot.repository;

import bot.awesome.bot.util.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByChatId(String chatId);
    List<User> findAllByChatId(String userName);
    Optional<User> findByUsername(String username);
    Optional<User> findByChatIdAndUserId(String chatId, String userId);
    void deleteByChatId(String chatId);
}
