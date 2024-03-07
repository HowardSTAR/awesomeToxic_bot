package bot.awesome.bot.service;

import bot.awesome.bot.repository.DailyPickRepository;
import bot.awesome.bot.repository.UserRepository;
import bot.awesome.bot.util.DailyPick;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

// Класс сервиса для работы с ежедневными выборами пользователей.
// Использует репозитории для взаимодействия с базой данных.
@Service
public class DailyPickService {

    private final DailyPickRepository dailyPickRepository;
    private final UserRepository userRepository;

    // В конструкторе происходит автоматическое подключение (инъекция) репозиториев.
    @Autowired
    public DailyPickService(DailyPickRepository dailyPickRepository, UserRepository userRepository) {
        this.dailyPickRepository = dailyPickRepository;
        this.userRepository = userRepository;
    }

    // Метод для сброса статистики выборов в определенном чате.
    // Использует транзакционную обработку для обеспечения целостности данных.
    @Transactional
    public void resetStatistics(String chatId) {
        // Полное удаление статистики выборов по идентификатору чата.
        dailyPickRepository.deleteStatByChatId(chatId);
    }

    // Метод для добавления ежедневного выбора пользователя.
    // Принимает идентификатор чата и идентификатор пользователя.
    @Transactional
    public void addDailyPick(String chatId, Long userId) {
        DailyPick dailyPick = new DailyPick();
        // Установка идентификатора чата для выбора.
        dailyPick.setChatId(chatId);
        // Установка идентификатора пользователя для выбора.
        dailyPick.setUserId(userId);
        // Установка текущей даты как даты выбора.
        dailyPick.setPickDate(LocalDate.now());
        // Сохранение выбора в базе данных.
        dailyPickRepository.save(dailyPick);
    }
}
