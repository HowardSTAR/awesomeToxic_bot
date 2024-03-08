package bot.awesome.bot.repository;

import bot.awesome.bot.util.DailyPick;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DailyPickRepository extends JpaRepository<DailyPick, Long> {
    List<DailyPick> findByChatId(String chatId);

    List<DailyPick> findByChatIdAndPickDate(String chatId, LocalDate date);

    boolean existsByChatIdAndPickDate(String username, LocalDate pickDate);

    void deleteStatByChatId(String chatId);
}
