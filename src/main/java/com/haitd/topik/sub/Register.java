package com.haitd.topik.sub;

import com.haitd.topik.entity.Account;
import com.haitd.topik.entity.ConsoleColors;
import com.haitd.topik.handler.ReadDataSaved;
import com.haitd.topik.handler.SaveAccountToFile;
import com.haitd.topik.handler.Utility;
import com.haitd.topik.serializable.Accounts;
import com.haitd.topik.serializable.SavedCookieAndCaptcha;
import com.haitd.topik.serializable.SemesterIndexAndForm;
import com.haitd.topik.tasks.RegisterTasks;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.nio.reactor.ConnectingIOReactor;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;

public class Register {
    private final Scanner scanner;
    private final int filterLevel;

    public Register(Scanner scanner, int filterLevel) {
        this.scanner = scanner;
        this.filterLevel = filterLevel;
    }

    public void start() {
        Accounts savedAccounts = (Accounts) ReadDataSaved.read("./resources/serialization/accounts-" + filterLevel + ".ser");
        SemesterIndexAndForm semesterIndexAndForm = (SemesterIndexAndForm) ReadDataSaved.read("./resources/serialization/form.ser");

        if (savedAccounts == null || semesterIndexAndForm == null) {
            System.out.println("Data Error");
            return;
        }
        List<Account> accounts = savedAccounts.getAccount();
        BasicCookieStore cookieStore = savedAccounts.getBasicCookieStore();

        String semesterIndex = semesterIndexAndForm.getSemesterIndex();
        Map<String, String> inputNameAndValuePair = semesterIndexAndForm.getInputNameAndValuePair();

        CloseableHttpAsyncClient httpAsyncClient = null;
        ConnectingIOReactor ioReactor = null;
        try {
            ioReactor = new DefaultConnectingIOReactor();
            PoolingNHttpClientConnectionManager manager = new PoolingNHttpClientConnectionManager(ioReactor);
            manager.setMaxTotal(accounts.size());
            httpAsyncClient = HttpAsyncClients.custom()
                    .setConnectionManager(manager)
                    .setDefaultCookieStore(cookieStore)
                    .build();
            httpAsyncClient.start();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        ForkJoinPool pool = new ForkJoinPool();

        while (true) {
            long startTime = System.currentTimeMillis();
            RegisterTasks registerTasks = new RegisterTasks(accounts, semesterIndex, inputNameAndValuePair, httpAsyncClient);
            pool.invoke(registerTasks);

            int countDone = 0;
            for (Account account : accounts) {
                if (account.isDone()) {
                    countDone++;
                }
            }
            System.out.println(ConsoleColors.ANSI_GREEN_BACKGROUND + "Register done " + countDone + "/" + accounts.size() + " Accounts in " + (System.currentTimeMillis() - startTime) / 1000 + " seconds" + ConsoleColors.ANSI_RESET);
            if (countDone < accounts.size()) {
                System.out.print("Continue? (Y/n) ");
                String selection = scanner.nextLine();
                if (selection.equals("n")) {
                    break;
                }
            } else {
                break;
            }
        }
        pool.shutdown();
        SaveAccountToFile.saveAccount(accounts, cookieStore, filterLevel);
    }
}
