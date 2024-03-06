package bot.awesome.bot.repository;

import bot.awesome.bot.util.DailyPick;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyPickRepository extends JpaRepository<DailyPick, Long> {
    // Методы для работы с данными, например, проверка существования записи для чата и даты
    List<DailyPick> findByChatId(String chatId);
    List<DailyPick> findByChatIdAndPickDate(String chatId, LocalDate date);
    boolean existsByChatIdAndPickDate(String username, LocalDate pickDate);
    void deleteStatByChatId(String chatId);
}
