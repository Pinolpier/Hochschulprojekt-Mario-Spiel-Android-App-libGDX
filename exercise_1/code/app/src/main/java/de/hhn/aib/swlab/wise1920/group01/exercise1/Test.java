package de.hhn.aib.swlab.wise1920.group01.exercise1;

import androidx.room.Room;
import java.util.Calendar;



class Test {

    private Calendar cal = Calendar.getInstance();
    private int id;
    private AppDatabase db;

    Test(MainActivity mainActivity) {
        db = Room.databaseBuilder(mainActivity.getApplicationContext(),
                AppDatabase.class, "database-timer").build();
    }

    void speichern (int hour, int minute) {
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        db.timerDao().insertTimer(new Timer(id, cal.getTimeInMillis(), true));
        id++;
    }
}
