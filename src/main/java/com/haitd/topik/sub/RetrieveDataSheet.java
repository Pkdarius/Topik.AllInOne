package com.haitd.topik.sub;

import com.google.api.services.drive.Drive;
import com.haitd.topik.entity.Account;
import com.haitd.topik.handler.SaveAccountToFile;
import com.haitd.topik.service.GoogleApiService;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class RetrieveDataSheet {
    private final int filterLevel;

    public RetrieveDataSheet(int filterLevel) {
        this.filterLevel = filterLevel;
    }

    public void start() throws GeneralSecurityException, IOException {
        GoogleApiService googleService = new GoogleApiService();
        Drive driveService = googleService.getDriveService();
        List<List<Object>> values = googleService.getDataFromSheet();
        String[] jobs = new String[]{"Sinh viên",
                "Công chức",
                "Nhân viên công ty",
                "Tự kinh doanh",
                "Nội trợ",
                "Giáo viên",
                "Thất nghiệp"};
        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
            return;
        }
        List<Account> accountList = new ArrayList<>();
        for (List<Object> row : values) {
            String level = (String) row.get(1);
            Account account = new Account();

            account.setLevel(level.equals("TOPIK I") ? "7" : "8");

            String email = ((String) row.get(3)).trim().toLowerCase();
            String[] emailPart = email.split("@");
            account.setEmailName(emailPart[0]);
            account.setEmailDomain(emailPart[1]);

            account.setFullName(((String) row.get(4)).toUpperCase());
            account.setKoreanName((String) row.get(5));

            String[] dateOfBirth = ((String) row.get(6)).split("-");
            account.setDayOfBirth(dateOfBirth[0]);
            account.setMonthOfBirth(dateOfBirth[1]);
            account.setYearOfBirth(dateOfBirth[2]);

            account.setSex(row.get(7).equals("Nam") ? "m" : "f");
            account.setSid((String) row.get(8));

            String job = (String) row.get(9);
            for (int i = 0; i < jobs.length; i++) {
                if (job.equals(jobs[i])) {
                    account.setJob((i + 1) + "");
                }
            }
            account.setPhone((String) row.get(10));
            account.setAddress((String) row.get(11));
            account.setPassword((String) row.get(13));

            String filePath = "resources/images/" + email + ".jpg";
            File file = new File(filePath);
            if (!file.exists()) {
                String driveImageId = ((String) row.get(2)).split("id=")[1];
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                driveService.files().get(driveImageId)
                        .executeMediaAndDownloadTo(byteArrayOutputStream);
                try (OutputStream outputStream = new FileOutputStream(file)) {
                    byteArrayOutputStream.writeTo(outputStream);
                }
            }
            accountList.add(account);
        }
        System.out.println("Total Accounts: " + accountList.size());
        SaveAccountToFile.saveAccount(accountList, null, filterLevel);
    }
}

