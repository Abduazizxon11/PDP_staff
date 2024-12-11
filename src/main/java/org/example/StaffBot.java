package org.example;

import org.example.modul.BotState;

import org.example.modul.User;
import org.example.repository.StatRepository;
import org.example.repository.UserRepository;
import org.example.service.ButtonService;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.CopyMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageId;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
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
                send(chatId, "Yaxshi. Endi hisobot faylini jo'nating");
                userRepository.updateState(chatId, BotState.SEND_REPORT);
            } else if(text.contains("cancel") || text.contains("accept")){
                String rChatId = text.substring(7);
                if (text.contains("cancel")){
                    send("1280496237", "Ushbu hisobot rad etildi va bu haqida o'qtuvchiga xabar yuborildi ✅");
                } else if (text.contains("accept")) {
                    send(rChatId, "Sizning hisobotingiz qabul qilindi \uD83E\uDD73");
                    send("1280496237", "Ushbu hisobot tasdiqlandi va bu haqida o'qtuvchiga xabar yuborildi ✅");
                }
            }
        } else if (update.getMessage().hasDocument()) {
            User user = userRepository.selectByChatId(update.getMessage().getChatId());
            if (user.getBotState() == BotState.SEND_REPORT) {

                send(update.getMessage().getChatId(), "Sizning hisobotingiz muvaffaqiyatli yuborildi. Javob kelishini kuting...");
            }
            userRepository.updateState(update.getMessage().getChatId(), BotState.PDP_START);
        } else if (update.hasMessage()) {
            Long chatId = update.getMessage().getChatId();
            String text = update.getMessage().getText();

            if (userRepository.selectByChatId(update.getMessage().getChatId()) == null) {
                userRepository.insertChatIdAndState(update.getMessage().getChatId());
            }
            User user = userRepository.selectByChatId(update.getMessage().getChatId());

            System.out.println(user.toString());
            if (text.equals("/start")) {
                if (user.getRole().equals("ADMIN")){
                    send(chatId, "Admin panelga xush kelibsiz", buttonService.adminMenu());
                    userRepository.updateState(chatId, BotState.ADMIN_START);
                } else {
                    if (user.getFullName() == null) {
                        send(chatId, "Assalomu aleykum. Ro'yxatdan o'tish uchun ism-familiyangizni kiriting");
                    }
                    else {
                        userRepository.updateState(update.getMessage().getChatId(), BotState.PDP_START);
                        send(chatId, "Assalomu aleykum. Quydagilardan birini tanlang yoki ism o'zgartirmoqchi bo'lsangiz yozib jo'nating", buttonService.userMenu());
                    }
                }
            } else if (text.contains("start")) {
                String key = text.substring(6);
                statRepository.create(update.getMessage().getChatId(), key);
                send(chatId, "Sizning bugungi maktabga kirgan vaqtingiz muvaffaqiyatli qo'shildi");
                System.out.println(user.getFullName());

                if (user.getFullName() == null) {
                    send(chatId, "Assalomu aleykum. Ro'yxatdan o'tish uchun ism-familiyangizni kiriting");
                    userRepository.updateState(update.getMessage().getChatId(), BotState.REGISTER);
                }
            } else {
                if (user.getBotState() == BotState.PDP_START) {
                    send(chatId, "Assalomu aleykum. Ro'yxatdan o'tish uchun ism-familiyangizni kiriting");
                    userRepository.updateState(update.getMessage().getChatId(), BotState.REGISTER);
                } else if (user.getBotState() == BotState.REGISTER) {
                    userRepository.updateFullName(chatId, text);
                    send(chatId, "Ismingiz muvaffaqiyatli kiritildi. Agar ismni o'zgartirmoqchi bo'lsangiz ismingizni yozib jo'nating yoki quydagilardan birini tanlang", buttonService.userMenu());
                } else if (user.getBotState() == BotState.SEND_REPORT) {
                    copy(update.getMessage().getChatId(), "1280496237", update.getMessage().getMessageId(), "Yuboruvchi Ism-familiyasi: " + user.getFullName(), buttonService.acceptButton(update.getMessage().getChatId()));
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
                        System.out.println(builder);
                        send(chatId, "Bugungi kelgan barcha xodimlar:\n" + builder);
                    } else if (text.equals("Sana bo'yicha tanlash \uD83D\uDCC5")) {
                        send(update.getMessage().getChatId(), "Sanani tanlang.\n ⚠\uFE0F ESLATMA: FAQATGINA DASTUR ISHLATILGAN KUNLARDANGINA HISOBOT OLISH MUMKIN", buttonService.selectWithDate(12));
                    } else if (text.equals("Yangi QR-Kod generatsiya qilish ")) {
                        userRepository.updateState(update.getMessage().getChatId(), BotState.PAGE_ADMIN_GENERATE_KEYWORD);

                    }
                }
            }
        }
    }

    private Message send(Long chatId, String text){
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
        try {
            return execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
    private Message send(String chatId, String text){
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
        try {
            return execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
    private Message send(Long chatId, String text, ReplyKeyboardMarkup replyMarkup){
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(replyMarkup)
                .build();
        try {
            return execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
    private Message send(Long chatId, String text, InlineKeyboardMarkup replyMarkup){
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(replyMarkup)
                .build();
        try {
            return execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
    private MessageId copy(Long fromChatId, String toChatId, Integer messageId, String caption, InlineKeyboardMarkup replyMarkup){
        CopyMessage message = new CopyMessage();
        message.setFromChatId(fromChatId);
        message.setChatId(toChatId);
        message.setMessageId(messageId);
        message.setCaption(caption);
        message.setReplyMarkup(replyMarkup);
        try {
            return execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getBotUsername() {
        return "pdp_staff_bot";
    }
}
