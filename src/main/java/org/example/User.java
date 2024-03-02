package org.example;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String chatId; // ID чата в Telegram

    private String username; // Никнейм пользователя

    private Integer beautyCount = 0; // Сколько раз пользователь был красавчиком дня

    // Конструкторы, геттеры и сеттеры
    public User() {}

    public User(String chatId) {
        this.chatId = chatId;
    }

    // Геттеры и сеттеры
    // Конструкторы, геттеры и сеттеры для нового поля
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

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

    public Integer getBeautyCount() {
        return beautyCount;
    }

    public void setBeautyCount(Integer beautyCount) {
        this.beautyCount = beautyCount;
    }
}


