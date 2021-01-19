package com.haitd.topik;

import com.haitd.topik.sub.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Scanner;

public class Main {
    public static final String DOMAIN = "http://topikhanoi.com";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter semester: ");
        String semesterNumber = scanner.nextLine();

        System.out.print("Enter Level (7 - 8 - 9): ");
        int level;
        while (true) {
            String levelInput = scanner.nextLine();
            try {
                level = Integer.parseInt(levelInput);
                if (level == 7 || level == 8) {
                    break;
                } else {
                    System.out.println("Level must be one of 7 or 8");
                }
            } catch (NumberFormatException e) {
                System.out.println("Level must be a Integer");
            }
        }

        CreateInstances createInstances = new CreateInstances(level);
        while (true) {
            System.out.print("Select an option (0-7):\n"
                    + "0. Download Images\n"
                    + "1. Start AWS Instances\n"
                    + "2. Upload Images\n"
                    + "3. Get Captcha\n"
                    + "4. Get Register Form\n"
                    + "5. Register\n"
                    + "6. Check Index\n"
                    + "7. Stop AWS Instances\n"
                    + "8. Exit\n"
                    + "Your option: ");
            int option;
            while (true) {
                String userOption = scanner.nextLine();
                try {
                    option = Integer.parseInt(userOption);
                    if (option < 0 || option > 8) {
                        System.out.print("Wrong option. Please enter correct option: ");
                    } else {
                        break;
                    }
                } catch (NumberFormatException e) {
                    System.out.print("Wrong option. Please enter correct option: ");
                }
            }

            if (option == 0) {
                RetrieveDataSheet sheet = new RetrieveDataSheet(level);
                try {
                    sheet.start();
                } catch (GeneralSecurityException | IOException e) {
                    e.printStackTrace();
                }
            }
            if (option == 1) {
                try {
                    createInstances.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (option == 2) {
                UploadImage uploadImage = new UploadImage(scanner, level);
                uploadImage.start();
            }
            if (option == 3) {
                GetCaptcha getCaptcha = new GetCaptcha(level);
                getCaptcha.start();
            }
            if (option == 4) {
                GetFormRegister getFormRegister = new GetFormRegister(semesterNumber);
                getFormRegister.start();
            }
            if (option == 5) {
                Register register = new Register(scanner, level);
                register.start();
            }
            if (option == 6) {
                CheckIndex checkIndex = new CheckIndex(level);
                checkIndex.start();
            }
            if (option == 7) {
                createInstances.stop();
            }
            if (option == 8) {
                break;
            }
            System.out.println();
        }
        scanner.close();
    }
}
