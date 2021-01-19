package com.haitd.topik.tasks;

import com.haitd.topik.Main;
import com.haitd.topik.entity.Account;
import com.haitd.topik.handler.Utility;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class CheckIndexTask extends RecursiveTask<List<Account>> {
    private final List<Account> accounts;

    public CheckIndexTask(List<Account> accounts) {
        this.accounts = accounts;
    }

    @Override
    protected List<Account> compute() {
        if (accounts.size() > 1) {
            List<RecursiveTask<List<Account>>> forks = new ArrayList<>();
            for (int i = 0; i < accounts.size(); i++) {
                RecursiveTask<List<Account>> subTask = new CheckIndexTask(accounts.subList(i, i + 1));
                subTask.fork();

                forks.add(subTask);
            }
            List<Account> result = new ArrayList<>();
            for (RecursiveTask<List<Account>> subTask: forks) {
                result.add(subTask.join().get(0));
            }
            return result;
        } else {
            return checkIndex();
        }
    }

    public List<Account> checkIndex() {
        Account account = accounts.get(0);
        try {
            try (CloseableHttpClient httpClient = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build()) {
                RequestBuilder builder = RequestBuilder.post(Main.DOMAIN + "/bbs/apply_manage_chk.php")
                        .addParameter("u_email0", account.getEmailName())
                        .addParameter("u_email1", account.getEmailDomain())
                        .addParameter("u_pwd", account.getPassword());
                HttpUriRequest httpPost = builder.build();
                String response = httpClient.execute(httpPost, Utility.responseHandler);
                String type = account.getLevel().equals("7") ? "TOPIK1" : "TOPIK2";
                String pattern = "<tr>\\s*<td.*>(\\d+)<\\/td>\\s*<td.*>.*<\\/td>\\s*<td.*>.*<\\/td>\\s*<td.*>" + type + "<\\/td>\\s*<td.*>.*<\\/td>\\s*<td.*>.*<\\/td>\\s*<td class=\"exam_txt\">(.+)(.*)</td>\\s*<td class=\"exam_txt\" ><span";
                String index = Utility.getIndex(response, pattern, 1);
                System.out.println(account.getEmailName() + " index: " + index);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.singletonList(account);
    }
}
