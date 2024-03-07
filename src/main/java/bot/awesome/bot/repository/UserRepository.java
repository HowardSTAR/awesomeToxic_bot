package bot.awesome.bot.repository;

import bot.awesome.bot.util.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/*
 Интерфейс для репозитория пользователей,
 предоставляющий методы для взаимодействия с данными пользователя в базе данных.
*/
public interface UserRepository extends JpaRepository<User, Long> {

    /*
         Находит пользователя по идентификатору чата.
         Возвращает объект Optional<User>, который может содержать пользователя, если он найден.
    */
    Optional<User> findByChatId(String chatId);

    /*
         Находит всех пользователей с указанным идентификатором чата.
         Возвращает список пользователей, связанных с данным идентификатором чата.
    */
    List<User> findAllByChatId(String chatId);

    /*
         Находит пользователя по его имени пользователя (username).
         Возвращает объект Optional<User>, который может содержать пользователя, если он найден.
    */
    Optional<User> findByUsername(String username);

    /*
         Находит пользователя по идентификатору чата и идентификатору пользователя (userId).
         Этот метод может использоваться для получения конкретного пользователя в рамках чата.
         Возвращает объект Optional<User>, который может содержать пользователя, если он найден.
    */
    Optional<User> findByChatIdAndUserId(String chatId, String userId);

    /*
         Удаляет всех пользователей с указанным идентификатором чата.
         Этот метод может использоваться для очистки данных о пользователях в чате.
    */
    void deleteByChatId(String chatId);
}
