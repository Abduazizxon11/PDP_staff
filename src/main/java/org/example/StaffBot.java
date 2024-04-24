package org.example;

import org.example.modul.BotState;

import org.example.modul.User;
import org.example.repository.StatRepository;
import org.example.repository.UserRepository;
import org.example.service.ButtonService;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.CopyMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

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
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            String text = update.getCallbackQuery().getData();
            if (text.equals("copy")){
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(chatId);
                sendMessage.setText("Yaxshi. Endi hisobot faylini jo'nating");
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
                userRepository.updateState(chatId, BotState.SEND_REPORT);
            } else if(text.contains("cancel") || text.contains("accept")){
                String rChatId = text.substring(7);
                if (text.contains("cancel")){
                    SendMessage message = new SendMessage();
                    message.setChatId(rChatId);
                    message.setText("Sizning hisobotingiz rad etildi.");
                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                } else if (text.contains("accept")) {
                    SendMessage message = new SendMessage();
                    message.setChatId(rChatId);
                    message.setText("Sizning hisobotingiz qabul qilindi.");
                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        } else if (update.getMessage().hasDocument()) {
            User user = userRepository.selectByChatId(update.getMessage().getChatId());
            if (user.getBotState() == BotState.SEND_REPORT) {
                CopyMessage message1 = new CopyMessage();
                message1.setFromChatId(update.getMessage().getChatId());
                message1.setChatId("1280496237");
                message1.setMessageId(update.getMessage().getMessageId());
                message1.setCaption("Yuboruvchi Ism-familiyasi: " + user.getFullName());
                message1.setReplyMarkup(buttonService.acceptButton(update.getMessage().getChatId()));
                try {
                    execute(message1);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
            userRepository.updateState(update.getMessage().getChatId(), BotState.PDP_START);
        } else if (update.hasMessage()) {
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
                    else {
                        userRepository.updateState(update.getMessage().getChatId(), BotState.PDP_START);
                        SendMessage sendMessage = new SendMessage();
                        sendMessage.setChatId(update.getMessage().getChatId());
                        sendMessage.setText("Assalomu aleykum. Quydagilardan birini tanlang yoki ism o'zgartirmoqchi bo'lsangiz yozib jo'nating");
                        sendMessage.setReplyMarkup(buttonService.userMenu());
                        try {
                            execute(sendMessage);
                        } catch (TelegramApiException e) {
                                throw new RuntimeException(e);
                        }
                    }
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
                    message.setText("Ismingiz muvaffaqiyatli kiritildi. Agar ismni o'zgartirmoqchi bo'lsangiz ismingizni yozib jo'nating yoki quydagilardan birini tanlang");
                    message.setReplyMarkup(buttonService.userMenu());
                } else if (user.getBotState() == BotState.SEND_REPORT) {
                    CopyMessage message1 = new CopyMessage();
                    message1.setFromChatId(update.getMessage().getChatId());
                    message1.setChatId("1280496237");
                    message1.setMessageId(update.getMessage().getMessageId());
                    message1.setCaption("Yuboruvchi Ism-familiyasi: " + user.getFullName());
                    try {
                        execute(message1);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                    userRepository.updateState(update.getMessage().getChatId(), BotState.PDP_START);
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