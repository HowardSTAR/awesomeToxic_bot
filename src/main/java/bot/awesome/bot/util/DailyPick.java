package bot.awesome.bot.util;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "public.daily_picks")
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

    // Конструктор
    public DailyPick() {}
}
