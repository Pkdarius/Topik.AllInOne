package com.haitd.topik.tasks;

import com.haitd.topik.Main;
import com.haitd.topik.entity.Account;
import com.haitd.topik.handler.Utility;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;
import java.util.stream.IntStream;

public class UploadImageTask extends RecursiveTask<List<Account>> {
    private final List<Account> accounts;
    private final CloseableHttpAsyncClient client;

    public UploadImageTask(List<Account> accounts, CloseableHttpAsyncClient client) {
        this.accounts = accounts;
        this.client = client;
    }

    @Override
    protected List<Account> compute() {
        if (this.accounts.size() > 1) {
            List<RecursiveTask<List<Account>>> forks = new ArrayList<>();
            List<Account> result = new ArrayList<>();
            for (Account account : accounts) {
                if (account.getImageId() != null) {
                    result.add(account);
                } else {
                    RecursiveTask<List<Account>> subTask = new UploadImageTask(Collections.singletonList(account), client);
                    subTask.fork();
                    forks.add(subTask);
                }
            }
            for (RecursiveTask<List<Account>> subTask : forks) {
                result.add(subTask.join().get(0));
            }
            return result;
        } else {
            return uploadImage();
        }
    }

    private List<Account> uploadImage() {
        Account account = accounts.get(0);
        try {
            File file = new File("resources/images/" + account.getEmailName() + "@" + account.getEmailDomain() + ".jpg");

            HttpEntity uploadImageEntity = MultipartEntityBuilder
                    .create()
                    .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                    .addBinaryBody("bf_file", new FileInputStream(file), ContentType.DEFAULT_BINARY, file.getName())
                    .addPart("w", new StringBody("", ContentType.TEXT_PLAIN))
                    .addPart("bf_no", new StringBody("", ContentType.TEXT_PLAIN)).build();

            BufferedHttpEntity bufferedUploadImageEntity = new BufferedHttpEntity(uploadImageEntity);

            HttpPost httpPost = new HttpPost("http://" + account.getIpAddress() + ":1248/bbs/ajax_img_upload.php");
//            HttpPost httpPost = new HttpPost("http://topikhanoi.com/bbs/ajax_img_upload.php");
            httpPost.setEntity(bufferedUploadImageEntity);
            Future<HttpResponse> uploadImageFuture = this.client.execute(httpPost, null);
            String uploadImageResponseString = Utility.GetEntityString(uploadImageFuture);
            String pattern = "(r_tmpidx\":)(\\d+)";
            String imgIndex = Utility.getIndex(uploadImageResponseString, pattern, 2);
            account.setImageId(imgIndex);
            System.out.println(account.getEmailName() + " upload Done");
        } catch (Exception e) {
            account.setImageId(null);
            System.out.println(e.getMessage());
            System.out.println(account.getEmailName() + " Upload Failed");
        }
        return accounts;
    }
}
