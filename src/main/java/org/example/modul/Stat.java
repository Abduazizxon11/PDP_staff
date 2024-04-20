package org.example.modul;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Stat {
    private long chatId;
    private LocalDateTime enterTime;
    private String keyword;

    public Stat(long chatId, LocalDateTime enterTime){
        this.chatId = chatId;
        this.enterTime = enterTime;
    }
}
