package org.example;

import org.example.modul.BotState;
import org.example.modul.Role;
import org.example.modul.User;
import org.example.service.impl.StatServiceImpl;
import org.example.service.impl.UserServiceImpl;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static org.example.modul.BotState.*;

public class StaffBot extends TelegramLongPollingBot {
    private final UserServiceImpl userService = new UserServiceImpl();
    private final StatServiceImpl statService = new StatServiceImpl();
    public StaffBot(String botToken) {
        super(botToken);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()){
            long chatId = update.getMessage().getChatId();
            String text = update.getMessage().getText();
            System.out.println("1");
            if (text.equals("/start")){
                SendMessage message = new SendMessage();
                message.setChatId(chatId);
                message.setText("Assalomu aleykum. Ro'yxatdan o'tish uchun ism familiyangizni kiriting");
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }

                User user = new User(
                        chatId,
                        update.getMessage().getText(),
                        Role.USER,
                        BotState.START
                );
                user = userService.create(user);
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "pdp_staff_bot";
    }
}
