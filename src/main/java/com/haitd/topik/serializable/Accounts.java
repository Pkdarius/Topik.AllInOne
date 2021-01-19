package com.haitd.topik.serializable;

import com.haitd.topik.entity.Account;
import org.apache.http.impl.client.BasicCookieStore;

import java.io.Serializable;
import java.util.List;

public class Accounts implements Serializable {
    private List<Account> account;
    private BasicCookieStore basicCookieStore;

    public Accounts(List<Account> account, BasicCookieStore basicCookieStore) {
        this.account = account;
        this.basicCookieStore = basicCookieStore;
    }

    public List<Account> getAccount() {
        return account;
    }

    public void setAccount(List<Account> account) {
        this.account = account;
    }

    public BasicCookieStore getBasicCookieStore() {
        return basicCookieStore;
    }

    public void setBasicCookieStore(BasicCookieStore basicCookieStore) {
        this.basicCookieStore = basicCookieStore;
    }
}
