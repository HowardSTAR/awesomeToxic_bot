package org.example.util;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String chatId; // ID чата в Telegram

    @Column
    private String userId; // ID пользователя в Telegram

    private String username; // Никнейм пользователя

    private Integer beautyCount = 0; // Сколько раз пользователь был красавчиком дня

    // Конструкторы, геттеры и сеттеры
    public User() {}

    public User(String chatId) {
        this.chatId = chatId;
    }
}


