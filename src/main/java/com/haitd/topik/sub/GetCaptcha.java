package com.haitd.topik.sub;

import com.haitd.topik.Main;
import com.haitd.topik.entity.Account;
import com.haitd.topik.entity.ConsoleColors;
import com.haitd.topik.handler.ReadDataSaved;
import com.haitd.topik.handler.SaveAccountToFile;
import com.haitd.topik.handler.Utility;
import com.haitd.topik.serializable.Accounts;
import com.haitd.topik.serializable.SavedCookieAndCaptcha;
import com.haitd.topik.service.CaptchaSolverBot;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GetCaptcha {
    private final int filterLevel;

    public GetCaptcha(int filterLevel) {
        this.filterLevel = filterLevel;
    }

    public void start() {
        ApiContextInitializer.init();

        TelegramBotsApi botsApi = new TelegramBotsApi();
        CaptchaSolverBot captchaSolverBot = new CaptchaSolverBot();

        try {
            botsApi.registerBot(captchaSolverBot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            return;
        }

        Logger logger = Logger.getLogger(HttpClient.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        Accounts savedAccounts = (Accounts) ReadDataSaved.read("./resources/serialization/accounts-" + filterLevel + ".ser");
        if (savedAccounts == null) {
            System.out.println("Data Error");
            return;
        }
        List<Account> accounts = savedAccounts.getAccount();
        BasicCookieStore cookieStore = savedAccounts.getBasicCookieStore();
        Update oldUpdate = null;
        for (Account account : accounts) {
            RequestConfig globalConfig = RequestConfig.custom()
                    .setCookieSpec("standard").build();

            CloseableHttpClient httpClient = HttpClients.custom()
                    .setDefaultRequestConfig(globalConfig)
                    .setDefaultCookieStore(cookieStore)
                    .build();

            HttpPost captchaRequest = new HttpPost("http://" + account.getIpAddress() + ":1248/plugin/kcaptcha/kcaptcha_session.php");
            HttpGet imageRequest = new HttpGet("http://" + account.getIpAddress() + ":1248/plugin/kcaptcha/kcaptcha_image.php");
            HttpPost checkCaptchaRequest = new HttpPost("http://" + account.getIpAddress() + ":1248/plugin/kcaptcha/kcaptcha_result.php");

//            HttpPost captchaRequest = new HttpPost("http://topikhanoi.com/plugin/kcaptcha/kcaptcha_session.php");
//            HttpGet imageRequest = new HttpGet("http://topikhanoi.com/plugin/kcaptcha/kcaptcha_image.php");
//            HttpPost checkCaptchaRequest = new HttpPost("http://topikhanoi.com/plugin/kcaptcha/kcaptcha_result.php");

            while (true) {
                try (CloseableHttpResponse ignored = httpClient.execute(captchaRequest);
                     CloseableHttpResponse response = httpClient.execute(imageRequest);
                     InputStream captchaInputStream = response.getEntity().getContent()) {
                    while (true) {
                        boolean isSendCaptcha = captchaSolverBot.sendCaptcha("1273718966", "captcha.jpg", captchaInputStream);
                        if (isSendCaptcha) {
                            break;
                        }
                    }
                    break;
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
                System.out.println(ConsoleColors.ANSI_RED_BACKGROUND + "Error while send captcha image. Retry..." + ConsoleColors.ANSI_RESET);
            }
            String captcha;

            while (true) {
                Update update = captchaSolverBot.update;
                if (update == null) {
                } else if (update != oldUpdate && update.getMessage().hasText()) {
                    oldUpdate = update;
                    captcha = update.getMessage().getText();

                    HttpEntity entity = MultipartEntityBuilder.create()
                            .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                            .addPart("captcha_key", new StringBody(captcha, ContentType.TEXT_PLAIN))
                            .build();
                    checkCaptchaRequest.setEntity(entity);

                    try (CloseableHttpResponse checkCaptchaResponse = httpClient.execute(checkCaptchaRequest)) {
                        HttpEntity responseEntity = checkCaptchaResponse.getEntity();
                        String entityString = EntityUtils.toString(responseEntity);
                        if (entityString.equals("1")) {
                            System.out.println(ConsoleColors.ANSI_GREEN_BACKGROUND + "Correct Captcha." + ConsoleColors.ANSI_RESET);
                            break;
                        }
                        System.out.print(ConsoleColors.ANSI_RED_BACKGROUND + "Incorrect Captcha. Retry: " + ConsoleColors.ANSI_RESET);
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                }

                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            account.setCaptcha(captcha);

            try {
                httpClient.close();
            } catch (
                    IOException e) {
                e.printStackTrace();
            }
        }
        SaveAccountToFile.saveAccount(accounts, cookieStore, filterLevel);
    }
}
