package org.example.service;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ButtonService {
    public ReplyKeyboardMarkup adminMenu() {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        List<KeyboardRow> rowList = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();


        KeyboardButton button2 = new KeyboardButton();
        button2.setText("Bugungi ro'yxat \uD83D\uDCD1");
        row1.add(button2);

        KeyboardButton button3 = new KeyboardButton();
        button3.setText("Sana bo'yicha tanlash \uD83D\uDCC5");
        row1.add(button3);


        rowList.add(row1);
        markup.setKeyboard(rowList);
        markup.setSelective(true);
        markup.setResizeKeyboard(true);
        markup.setOneTimeKeyboard(true);

        return markup;
    }
    public InlineKeyboardMarkup selectWithDate(int month) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month - 1);

        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        List<InlineKeyboardButton> row = new ArrayList<>();
        for (int day = 1; day <= daysInMonth; day++) {
            if (day % 5 == 0){
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(day + "-" + month + "-2024");
                button.setCallbackData(day + "-" + month + "-2024");
                row.add(button);
                rowList.add(row);
                row = new ArrayList<>();
            } else {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(day + "-" + month + "-2024");
                button.setCallbackData(day + "-" + month + "-2024");
                row.add(button);
            }
        }
        markup.setKeyboard(rowList);
        return markup;
    }
    public InlineKeyboardMarkup userMenu(){
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText("Hisobot jo'natish");
        button1.setCallbackData("copy");

        row.add(button1);
        rowList.add(row);
        markup.setKeyboard(rowList);

        return markup;
    }
    public InlineKeyboardMarkup acceptButton(long chatId){
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText("Tasdiqlash");
        button1.setCallbackData("accept_" + chatId);

        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText("Rad etish");
        button2.setCallbackData("cancel_" + chatId);

        row.add(button1);
        row.add(button2);

        rowList.add(row);
        markup.setKeyboard(rowList);

        return markup;
    }
}
