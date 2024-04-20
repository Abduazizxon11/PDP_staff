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
//        KeyboardRow row2 = new KeyboardRow();


        KeyboardButton button2 = new KeyboardButton();
        button2.setText("Bugungi ro'yxat \uD83D\uDCD1");
        row1.add(button2);

        KeyboardButton button3 = new KeyboardButton();
        button3.setText("Sana bo'yicha tanlash \uD83D\uDCC5");
        row1.add(button3);

//        KeyboardButton button4 = new KeyboardButton();
//        button4.setText("Yangi QR-Kod generatsiya qilish");
//        row2.add(button4);

        rowList.add(row1);
//        rowList.add(row2);
        markup.setKeyboard(rowList);
        markup.setSelective(true);
        markup.setResizeKeyboard(true);
        markup.setOneTimeKeyboard(true);

        return markup;
    }
    public InlineKeyboardMarkup selectWithDate(int month) {//default 0 bo'ladi
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month - 1); // Month is 0-based

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
    public ReplyKeyboardMarkup userMenu(){
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        List<KeyboardRow> rowList = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();

        KeyboardButton button = new KeyboardButton();
        button.setText("Ism-familiyani o'zgartirish");
        row.add(button);
        rowList.add(row);
        markup.setKeyboard(rowList);
        markup.setSelective(true);
        markup.setResizeKeyboard(true);
        markup.setOneTimeKeyboard(true);

        return markup;
    }
}
