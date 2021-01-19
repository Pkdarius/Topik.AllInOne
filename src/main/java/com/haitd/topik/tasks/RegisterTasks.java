package com.haitd.topik.tasks;

import com.haitd.topik.entity.Account;
import com.haitd.topik.entity.ConsoleColors;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

public class RegisterTasks extends RecursiveTask<List<Account>> {
    private final List<Account> accounts;
    private final String semesterIndex;
    private final Map<String, String> inputNameAndValuePair;
    private final CloseableHttpAsyncClient httpAsyncClient;

    public RegisterTasks(List<Account> accounts,
                         String semesterIndex,
                         Map<String, String> inputNameAndValuePair,
                         CloseableHttpAsyncClient httpAsyncClient) {
        this.accounts = accounts;
        this.semesterIndex = semesterIndex;
        this.inputNameAndValuePair = inputNameAndValuePair;
        this.httpAsyncClient = httpAsyncClient;
    }

    @Override
    protected List<Account> compute() {
        if (this.accounts.size() > 1) {
            List<RecursiveTask<List<Account>>> forks = new ArrayList<>();
            List<Account> result = new ArrayList<>();
            for (Account account : accounts) {
                if (account.isDone()) {
                    result.add(account);
                } else {
                    RecursiveTask<List<Account>> subTask = new RegisterTasks(Collections.singletonList(account), semesterIndex, inputNameAndValuePair, httpAsyncClient);
                    subTask.fork();
                    forks.add(subTask);
                }
            }
            for (RecursiveTask<List<Account>> subTask : forks) {
                result.add(subTask.join().get(0));
            }
            return result;
        } else {
            return register();
        }
    }

    private List<Account> register() {
        Account account = accounts.get(0);

        Future<HttpResponse> responseFuture;
        String ipAddress = account.getIpAddress();
        HttpPost sendFormHttpPost = new HttpPost("http://" + ipAddress + ":1248/bbs/apply_reg_update.php");
        try {
            Map<String, StringBody> entityParts = inputNameAndValuePair.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> new StringBody(e.getValue(), ContentType.TEXT_PLAIN)));

            entityParts.put("imgidx", new StringBody(account.getImageId(), ContentType.TEXT_PLAIN));
            entityParts.put("a_level", new StringBody(account.getLevel(), ContentType.TEXT_PLAIN));
            entityParts.put("u_email0", new StringBody(account.getEmailName(), ContentType.TEXT_PLAIN));
            entityParts.put("u_email1", new StringBody(account.getEmailDomain(), ContentType.TEXT_PLAIN));
            entityParts.put("u_pwd", new StringBody(account.getPassword(), ContentType.TEXT_PLAIN));
            entityParts.put("u_pwd_confirm", new StringBody(account.getPassword(), ContentType.TEXT_PLAIN));
            entityParts.put("u_surname", new StringBody(account.getFullName(), ContentType.TEXT_PLAIN));
            entityParts.put("u_kname", new StringBody(account.getKoreanName(), ContentType.TEXT_PLAIN.withCharset("UTF-8")));
            entityParts.put("u_birth0", new StringBody(account.getYearOfBirth(), ContentType.TEXT_PLAIN));
            entityParts.put("u_birth1", new StringBody(account.getMonthOfBirth(), ContentType.TEXT_PLAIN));
            entityParts.put("u_birth2", new StringBody(account.getDayOfBirth(), ContentType.TEXT_PLAIN));
            entityParts.put("u_age", new StringBody("" + (Calendar.getInstance().get(Calendar.YEAR) - Integer.parseInt(account.getYearOfBirth())), ContentType.TEXT_PLAIN));
            entityParts.put("u_sex", new StringBody(account.getSex(), ContentType.TEXT_PLAIN));
            entityParts.put("u_nation", new StringBody("VNM", ContentType.TEXT_PLAIN));
            entityParts.put("u_sid", new StringBody(account.getSid(), ContentType.TEXT_PLAIN));
            entityParts.put("u_job", new StringBody(account.getJob(), ContentType.TEXT_PLAIN));
            entityParts.put("u_job_etc", new StringBody("", ContentType.TEXT_PLAIN));
            entityParts.put("u_tel", new StringBody(account.getPhone(), ContentType.TEXT_PLAIN));
            entityParts.put("u_hp", new StringBody(account.getPhone(), ContentType.TEXT_PLAIN));
            entityParts.put("u_addr", new StringBody(account.getAddress(), ContentType.TEXT_PLAIN.withCharset("UTF-8")));
            entityParts.put("u_motive", new StringBody(((int) (Math.random() * 8) + 1) + "", ContentType.TEXT_PLAIN));
            entityParts.put("u_motive_etc", new StringBody("", ContentType.TEXT_PLAIN));
            entityParts.put("u_purpose", new StringBody(((int) (Math.random() * 9) + 1) + "", ContentType.TEXT_PLAIN));
            entityParts.put("u_purpose_etc", new StringBody("", ContentType.TEXT_PLAIN));
            entityParts.put("checkbox", new StringBody("on", ContentType.TEXT_PLAIN));
            entityParts.put("checkbox2", new StringBody("on", ContentType.TEXT_PLAIN));
            entityParts.put("captcha_key", new StringBody(account.getCaptcha(), ContentType.TEXT_PLAIN));

            MultipartEntityBuilder builder = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            entityParts.forEach((key, value) -> {
                if (!key.isBlank()) {
                    builder.addPart(key, value);
                }
            });

            HttpEntity entity = builder.build();
            sendFormHttpPost.setEntity(entity);

            responseFuture = httpAsyncClient.execute(sendFormHttpPost, null);

            HttpResponse response = responseFuture.get();
            int code = response.getStatusLine().getStatusCode();
            account.setDone(code == 302);
            String html = IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);

            System.out.println(html);
            System.out.println(ConsoleColors.ANSI_GREEN_BACKGROUND + account.getEmailName() + " " + account.isDone() + ConsoleColors.ANSI_RESET);
            sendFormHttpPost.releaseConnection();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            account.setDone(false);
            System.out.println(ConsoleColors.ANSI_RED_BACKGROUND + account.getEmailName() + " fail" + ConsoleColors.ANSI_RESET);
        }
        return accounts;
    }
}
