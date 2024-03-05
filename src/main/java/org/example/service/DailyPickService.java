package org.example.service;

import org.example.util.DailyPick;
import org.example.repository.DailyPickRepository;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

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
    public void resetStatistics() {
        // Полное удаление статистики выборов
        dailyPickRepository.deleteAll();
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
