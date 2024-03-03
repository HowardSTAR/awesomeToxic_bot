package org.example.repository;

import org.example.util.DailyPick;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DailyPickRepository extends JpaRepository<DailyPick, Long> {
    // Методы для работы с данными, например, проверка существования записи для чата и даты
    boolean existsByChatIdAndPickDate(String chatId, LocalDate pickDate);
    List<DailyPick> findByChatId(String chatId);}