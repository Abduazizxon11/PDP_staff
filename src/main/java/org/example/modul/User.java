package org.example.modul;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class User {
    private long id;
    private long chatId;
    private String fullName;
    private Role role;
    private BotState state;

    public User(long chatId, String fullName, Role role, BotState state) {
        this.chatId = chatId;
        this.fullName = fullName;
        this.role = role;
        this.state = state;
    }
}
