package bot.awesome.bot.repository;

import bot.awesome.bot.util.DailyPick;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Интерфейс репозитория для объектов DailyPick.
 * Наследуется от JpaRepository, что предоставляет стандартный набор CRUD операций для работы с базой данных.
 */
public interface DailyPickRepository extends JpaRepository<DailyPick, Long> {

    /**
     * Поиск всех ежедневных выборов по идентификатору чата.
     *
     * @param chatId Идентификатор чата для поиска.
     * @return Список объектов DailyPick, соответствующих заданному chatId.
     */
    List<DailyPick> findByChatId(String chatId);

    /**
     * Поиск ежедневных выборов по идентификатору чата и дате.
     *
     * @param chatId Идентификатор чата для поиска.
     * @param date   Дата для поиска.
     * @return Список объектов DailyPick, соответствующих заданному chatId и дате.
     */
    List<DailyPick> findByChatIdAndPickDate(String chatId, LocalDate date);

    /**
     * Проверка существования ежедневного выбора по идентификатору чата и дате.
     *
     * @param username Идентификатор чата для проверки.
     * @param pickDate Дата для проверки.
     * @return true, если существует хотя бы один ежедневный выбор с заданным chatId и датой, иначе false.
     */
    boolean existsByChatIdAndPickDate(String username, LocalDate pickDate);

    /**
     * Удаление всех статистик ежедневных выборов по идентификатору чата.
     * Этот метод может потребовать реализации, так как JpaRepository не предоставляет его по умолчанию.
     *
     * @param chatId Идентификатор чата, для которого нужно удалить статистику.
     */
    void deleteStatByChatId(String chatId);
}
