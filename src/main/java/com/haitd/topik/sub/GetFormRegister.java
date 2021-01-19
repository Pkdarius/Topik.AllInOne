package com.haitd.topik.sub;

import com.haitd.topik.entity.ConsoleColors;
import com.haitd.topik.serializable.SemesterIndexAndForm;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class GetFormRegister {
    private final String semesterNumber;

    public GetFormRegister(String semesterNumber) {
        this.semesterNumber = semesterNumber;
    }

    public void start() {

        AtomicReference<String> semesterIndex = new AtomicReference<>();
        Map<String, String> inputNameAndValuePair;
        while (true) {
            try {
                Document registerList = Jsoup.connect("http://topikhanoi.com/bbs/apply.php").get();
//                Element theButton = registerList
//                        .select("#tk_idx").first()
//                        .getElementsByTag("option").select("[selected=selected]").first();
//                semesterIndex.set(theButton.val());

                Elements lines = registerList.select("#tk_idx").first()
                        .getElementsByTag("option");
                lines.forEach(line -> {
                    if(line.text().contains(semesterNumber)) {
                        semesterIndex.set(line.val());
                    }
                });

                System.out.println(ConsoleColors.ANSI_GREEN_BACKGROUND + "Get semester index done!" + ConsoleColors.ANSI_RESET);
                break;
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
                System.out.println(ConsoleColors.ANSI_GREEN_BACKGROUND + "Get semester index failed!" + ConsoleColors.ANSI_RESET);
            }
        }

        while (true) {
            try {
                Document registerForm = Jsoup.connect("http://topikhanoi.com/bbs/apply_reg.php?t_idx=" + semesterIndex).get();
                Elements inputElements = registerForm.getElementsByTag("input");
                inputNameAndValuePair = new HashMap<>();
                for (Element element : inputElements) {
                    inputNameAndValuePair.put(element.attr("name"), element.attr("value"));
                    System.out.println("! " + element.attr("name") + " " +  element.attr("value"));
                }
                System.out.println(ConsoleColors.ANSI_GREEN_BACKGROUND + "Get Register form done!" + ConsoleColors.ANSI_RESET);
                break;
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
                System.out.println(ConsoleColors.ANSI_RED_BACKGROUND + "Get Register form failed!" + ConsoleColors.ANSI_RESET);
            }
        }

        SemesterIndexAndForm savedData = new SemesterIndexAndForm(semesterIndex.get(), inputNameAndValuePair);
        try {
            FileOutputStream fileOut = new FileOutputStream("./resources/serialization/form.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(savedData);
            out.close();
            fileOut.close();
            System.out.println("Serialized accounts is saved in ./resources/serialization/form.ser");
        } catch (IOException i) {
            i.printStackTrace();
        }
    }
}
