package com.haitd.topik.sub;

import com.haitd.topik.entity.Account;
import com.haitd.topik.handler.ReadDataSaved;
import com.haitd.topik.serializable.Accounts;
import com.haitd.topik.tasks.CheckIndexTask;

import java.util.List;
import java.util.concurrent.ForkJoinPool;

public class CheckIndex {
    private final int filterLevel;

    public CheckIndex(int filterLevel) {
        this.filterLevel = filterLevel;
    }

    public void start() {
        Accounts savedAccounts = (Accounts) ReadDataSaved.read("./resources/serialization/accounts-" + filterLevel + ".ser");
        List<Account> accounts = savedAccounts.getAccount();

        ForkJoinPool pool = new ForkJoinPool();

        CheckIndexTask checkIndexTask = new CheckIndexTask(accounts);
        pool.invoke(checkIndexTask);
    }
}
