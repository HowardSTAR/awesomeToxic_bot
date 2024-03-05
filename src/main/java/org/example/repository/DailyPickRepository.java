package org.example.repository;

import org.example.util.DailyPick;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyPickRepository extends JpaRepository<DailyPick, Long> {
    // Методы для работы с данными, например, проверка существования записи для чата и даты
    boolean existsByChatIdAndPickDate(String username, LocalDate pickDate);
    List<DailyPick> findByChatId(String chatId);
    // TODO на считывание с таблицы кто сейчас красавчик дня
    Optional<DailyPick> findByChatIdAndPickDate(String chatId, LocalDate date);
}
