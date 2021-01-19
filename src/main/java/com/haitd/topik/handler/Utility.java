package com.haitd.topik.handler;

import com.haitd.topik.entity.Account;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ResponseHandler;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utility {
    public static final ResponseHandler<String> responseHandler = response -> {
        HttpEntity responseEntity = response.getEntity();
        return responseEntity != null ? EntityUtils.toString(responseEntity) : "";
    };

    public static final ResponseHandler<Integer> getStatusCode = httpResponse -> {
        StatusLine statusLine = httpResponse.getStatusLine();
        if (statusLine != null) {
            return httpResponse.getStatusLine().getStatusCode();
        }
        return 0;
    };

    public static String getIndex(String body, String pattern, int groupIndex) {
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(body);

        if (m.find()) {
            return m.group(groupIndex);
        }
        return "";
    }

    public static String GetEntityString(Future<HttpResponse> future) throws ExecutionException, InterruptedException, IOException {
        HttpResponse response = future.get();
        HttpEntity entity = response.getEntity();
        return EntityUtils.toString(entity);
    }

    public static String getIndex2(String body, String pattern, int groupIndex) {
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(body);
        if (m.find()) {
            return m.group(groupIndex);
        }
        return "";
    }
}

