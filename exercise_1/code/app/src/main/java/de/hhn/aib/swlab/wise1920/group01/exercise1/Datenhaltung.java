package de.hhn.aib.swlab.wise1920.group01.exercise1;

import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Scanner;

public class Datenhaltung {
    File file;
    Calendar test;

    public Datenhaltung(MainActivity mainActivity) {
        test = Calendar.getInstance();
        test.set(Calendar.HOUR_OF_DAY, 12);
        test.set(Calendar.MINUTE, 20);
        test.set(Calendar.SECOND, 0);
        file = new File(mainActivity.getFilesDir(), "Timers.txt");
    }


    public void saveTimer(Calendar timer) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.print(timer.getTimeInMillis()+"#");
            Log.d("speichern", String.valueOf(timer.getTimeInMillis()));
        }
    }

    public void getTimer() throws IOException {
        Calendar timergeladen;
        String timeInMillisString;
        try (Scanner scanner = new Scanner(file)) {
            scanner.useDelimiter("#");
            timeInMillisString = scanner.next();
            timergeladen = Calendar.getInstance();
            timergeladen.setTimeInMillis(Long.parseLong(timeInMillisString));
            Log.d("laden", String.valueOf(timergeladen.getTime()));
        }
    }
}
