package org.example;

import org.springframework.data.annotation.Id;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "daily_picks")
public class DailyPick {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chat_id")
    private String chatId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "pick_date")
    private LocalDate pickDate;

    // Конструкторы
    public DailyPick() {}

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDate getPickDate() {
        return pickDate;
    }

    public void setPickDate(LocalDate pickDate) {
        this.pickDate = pickDate;
    }
}
