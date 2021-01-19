package com.haitd.topik.handler;

import com.haitd.topik.entity.Account;
import com.haitd.topik.serializable.Accounts;
import org.apache.http.impl.client.BasicCookieStore;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

public class SaveAccountToFile {
    public static void saveAccount(List<Account> accounts, BasicCookieStore cookieStore, int filterLevel) {
        try {
            FileOutputStream fileOut = new FileOutputStream("./resources/serialization/accounts-" + filterLevel + ".ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);

            Accounts serializableAccounts = new Accounts(accounts, cookieStore);
            out.writeObject(serializableAccounts);

            out.close();
            fileOut.close();
            System.out.println("Serialized accounts is saved in ./resources/serialization/accounts-" + filterLevel + ".ser");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
