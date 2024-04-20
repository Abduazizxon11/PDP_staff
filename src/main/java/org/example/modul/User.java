package org.example.modul;

public class User {
    private long chatId;
    private String fullName;
    private String role;
    private BotState botState;

    @Override
    public String toString() {
        return  "chatId: " + chatId +
                ", fullName: '" + fullName + '\n' +
                ", role: '" + role + '\n' +
                ", botState: '" + botState + '\n'
                ;
    }

    public User(long chatId, String fullName, String role, BotState botState) {
        this.chatId = chatId;
        this.fullName = fullName;
        this.role = role;
        this.botState = botState;
    }

    public User() {
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public BotState getBotState() {
        return botState;
    }

    public void setBotState(BotState botState) {
        this.botState = botState;
    }
}
