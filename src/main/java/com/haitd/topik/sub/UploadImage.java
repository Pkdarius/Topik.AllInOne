package com.haitd.topik.sub;

import com.haitd.topik.entity.Account;
import com.haitd.topik.entity.ConsoleColors;
import com.haitd.topik.handler.ReadDataSaved;
import com.haitd.topik.handler.SaveAccountToFile;
import com.haitd.topik.serializable.Accounts;
import com.haitd.topik.tasks.UploadImageTask;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.nio.reactor.ConnectingIOReactor;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;

public class UploadImage {
    private final int filterLevel;
    private final Scanner scanner;

    public UploadImage(Scanner scanner, int filterLevel) {
        this.scanner = scanner;
        this.filterLevel = filterLevel;
    }

    public void start() {
        Accounts savedAccounts = (Accounts) ReadDataSaved.read("./resources/serialization/accounts-" + filterLevel + ".ser");
        if (savedAccounts == null) {
            System.out.println("No data");
            return;
        }
        List<Account> accounts = savedAccounts.getAccount();
        if (accounts == null || accounts.isEmpty()) {
            System.out.println("No data");
            return;
        }

        CloseableHttpAsyncClient httpAsyncClient;
        ConnectingIOReactor ioReactor;
        BasicCookieStore cookieStore = new BasicCookieStore();

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
            UploadImageTask uploadImageTasks = new UploadImageTask(accounts, httpAsyncClient);
            pool.invoke(uploadImageTasks);
            int count = 0;
            for(Account account: accounts) {
                if (account.getImageId() != null) {
                    count ++;
                }
            }
            System.out.println(ConsoleColors.ANSI_GREEN_BACKGROUND + "Upload " + count + "/" + accounts.size() + "." + ConsoleColors.ANSI_RESET);
            if (count < accounts.size()) {
                System.out.println("Continue upload? (Y/n)");
                String select = scanner.nextLine();
                if (select.equals("n")) {
                    break;
                }
            } else {
                break;
            }
        }

        pool.shutdown();
        try {
            httpAsyncClient.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        SaveAccountToFile.saveAccount(accounts, cookieStore, filterLevel);
    }
}
