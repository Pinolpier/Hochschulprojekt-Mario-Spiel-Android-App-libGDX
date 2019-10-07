package de.hhn.aib.swlab.wise1920.group01.exercise1;

import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Scanner;

class Datenhaltung {
    private Gson gson = new Gson();
    private File file;
    private Calendar test;
    private String json;

    Datenhaltung(MainActivity mainActivity) {
        file = new File(mainActivity.getFilesDir(), "Timer.txt");
        test = Calendar.getInstance();
        test.set(Calendar.HOUR_OF_DAY, 12);
        test.set(Calendar.MINUTE, 20);
        test.set(Calendar.SECOND, 0);
    }


    void saveTimer(Timer timer) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            json = gson.toJson(timer);
            writer.print(json+"#");
        }
    }

    Timer getTimer() throws IOException {
        try (Scanner scanner = new Scanner(file)) {
            scanner.useDelimiter("#");
            return gson.fromJson(json, Timer.class);
        }
    }
}
