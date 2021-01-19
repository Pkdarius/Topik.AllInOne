package com.haitd.topik.service;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.InputStream;

public class CaptchaSolverBot extends TelegramLongPollingBot {
    public Update update = null;

    @Override
    public void onUpdateReceived(Update update) {
        this.update = update;
    }

    @Override
    public String getBotUsername() {
        return "fucking_captcha_solver_bot";
    }

    @Override
    public String getBotToken() {
        return "1253965096:AAGjQYHNo1OClFG0WxTMaogbTGVySrWPiF8";
    }

    public boolean sendCaptcha(String chatId, String name, InputStream stream) {
        SendPhoto photo = new SendPhoto()
                .setChatId(chatId)
                .setPhoto(name, stream);
        try {
            execute(photo);
            return true;
        } catch (TelegramApiException e) {
            e.printStackTrace();
            return false;
        }
    }
}
