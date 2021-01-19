package com.haitd.topik.serializable;

import org.apache.http.impl.client.BasicCookieStore;

import java.io.Serializable;

public class SavedCookieAndCaptcha implements Serializable {
    private String captcha;
    private BasicCookieStore cookieStore;


    public SavedCookieAndCaptcha(String captcha, BasicCookieStore cookieStore) {
        this.captcha = captcha;
        this.cookieStore = cookieStore;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public BasicCookieStore getCookieStore() {
        return cookieStore;
    }

    public void setCookieStore(BasicCookieStore cookieStore) {
        this.cookieStore = cookieStore;
    }
}
