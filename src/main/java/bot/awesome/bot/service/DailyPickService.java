package bot.awesome.bot.service;

import bot.awesome.bot.repository.DailyPickRepository;
import bot.awesome.bot.repository.UserRepository;
import bot.awesome.bot.util.DailyPick;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class DailyPickService {

    private final DailyPickRepository dailyPickRepository;
    private final UserRepository userRepository;

    @Autowired
    public DailyPickService(DailyPickRepository dailyPickRepository, UserRepository userRepository) {
        this.dailyPickRepository = dailyPickRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void resetStatistics(String chatId) {
        // Полное удаление статистики выборов
        dailyPickRepository.deleteStatByChatId(chatId);
    }

    @Transactional
    public void addDailyPick(String chatId, Long userId) {
        DailyPick dailyPick = new DailyPick();
        dailyPick.setChatId(chatId);
        dailyPick.setUserId(userId);
        dailyPick.setPickDate(LocalDate.now());
        dailyPickRepository.save(dailyPick);
    }
}
