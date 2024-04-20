package org.example;

import org.example.modul.BotState;

import org.example.modul.User;
import org.example.repository.StatRepository;
import org.example.repository.UserRepository;
import org.example.service.ButtonService;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramBot;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class StaffBot extends TelegramLongPollingBot {
    private final UserRepository userRepository = new UserRepository();
    private final StatRepository statRepository = new StatRepository();
    private final ButtonService buttonService = new ButtonService();

    public StaffBot(String botToken) {
        super(botToken);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {

        } else if (update.hasMessage() && update.getMessage().hasText()) {
            if (userRepository.selectByChatId(update.getMessage().getChatId()) == null) {
                userRepository.insertChatIdAndState(update.getMessage().getChatId());
            }
            User user = userRepository.selectByChatId(update.getMessage().getChatId());

            SendMessage message = new SendMessage();
            message.setChatId(update.getMessage().getChatId());
            System.out.println(user.toString());
            String text = update.getMessage().getText();
            if (text.equals("/start")) {
                if (user.getRole().equals("ADMIN")){
                    message.setText("Admin panelga xush kelibsiz");
                    message.setReplyMarkup(buttonService.adminMenu());
                    userRepository.updateState(update.getMessage().getChatId(), BotState.ADMIN_START);
                } else {
                    if (user.getFullName() == null) {
                        message.setText("Assalomu aleykum. Ro'yxatdan o'tish uchun ism-familiyangizni kiriting");
                        userRepository.updateState(update.getMessage().getChatId(), BotState.REGISTER);
                    }
//                    else {
//                        SendMessage sendMessage = new SendMessage();
//                        sendMessage.setChatId(update.getMessage().getChatId());
//                        sendMessage.setText("Assalomu aleykum. Quydagilardan birini tanlang");
//                        sendMessage.setReplyMarkup(buttonService.userMenu());
//                        try {
//                            execute(sendMessage);
//                        } catch (TelegramApiException e) {
//                                throw new RuntimeException(e);
//                        }
//                        if (text.equals("Ism-familiyani o'zgartirish")){
//                            SendMessage message1 = new SendMessage();
//                            message1.setChatId(update.getMessage().getChatId());
//                            message1.setText("O'zgartirish uchun ism-familiyangizni kiriting");
//                            try {
//                                execute(message1);
//                            } catch (TelegramApiException e) {
//                                throw new RuntimeException(e);
//                            }
//                            userRepository.updateFullName(update.getMessage().getChatId(), text);
//                        }
//                    }
                }
            } else if (text.contains("start")) {
                String key = text.substring(6);
                statRepository.create(update.getMessage().getChatId(), key);
                message.setText("Sizning bugungi maktabga kirgan vaqtingiz muvaffaqiyatli qo'shildi");
                System.out.println(user.getFullName());
                if (user.getFullName() == null) {
                    message.setText("Assalomu aleykum. Ro'yxatdan o'tish uchun ism-familiyangizni kiriting");
                    userRepository.updateState(update.getMessage().getChatId(), BotState.REGISTER);
                }
            } else {
                if (user.getBotState() == BotState.PDP_START) {
                    message.setText("Assalomu aleykum. Ro'yxatdan o'tish uchun ism-familiyangizni kiriting");
                    userRepository.updateState(update.getMessage().getChatId(), BotState.REGISTER);
                } else if (user.getBotState() == BotState.REGISTER) {
                    userRepository.updateFullName(update.getMessage().getChatId(), update.getMessage().getText());
                    message.setText("Ismingiz muvaffaqiyatli kiritildi");
                } else if (user.getRole().equals("ADMIN") && user.getBotState() == BotState.ADMIN_START) {
                    if (text.equals("Bugungi ro'yxat \uD83D\uDCD1")) {
                        List<UserDTO> userDTOS = userRepository.selectWithStat();
                        System.out.println(userDTOS.toString());
                        StringBuilder builder = new StringBuilder();
                        for (int i = 1; i < userDTOS.size(); i++) {
                            builder.append(i)
                                    .append(". ")
                                    .append(userDTOS.get(i).getFullName())
                                    .append(" - ")
                                    .append(userDTOS.get(i).getEnterTime())
                                    .append("\n");
                        }
                        System.out.println(builder.toString());
                        message.setText("Bugungi kelgan barcha xodimlar:\n" + builder.toString());
                    } else if (text.equals("Sana bo'yicha tanlash \uD83D\uDCC5")) {
                        SendMessage sendMessage = new SendMessage();
                        sendMessage.setChatId(update.getMessage().getChatId());
                        sendMessage.setText("Sanani tanlang.\n ⚠\uFE0F ESLATMA: FAQATGINA DASTUR ISHLATILGAN KUNLARDANGINA HISOBOT OLISH MUMKIN");
                        sendMessage.setReplyMarkup(buttonService.selectWithDate(4));
                        try {
                            execute(sendMessage);
                        } catch (TelegramApiException e) {
                            throw new RuntimeException(e);
                        }
                    } else if (text.equals("Yangi QR-Kod generatsiya qilish ")) {
                        userRepository.updateState(update.getMessage().getChatId(), BotState.PAGE_ADMIN_GENERATE_KEYWORD);

                    }
                }
            }
            try {
                execute(message);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "pdp_staff_bot";
    }
}