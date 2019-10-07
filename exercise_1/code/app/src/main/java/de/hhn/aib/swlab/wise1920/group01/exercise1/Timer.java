package de.hhn.aib.swlab.wise1920.group01.exercise1;

import java.util.Calendar;
import java.util.Date;

public class Timer {
    private long time;
    private int id;
    private int active;

    Timer(Calendar cal, int id, int active) {
        this.time = cal.getTimeInMillis();
        this.id = id;
        this.active = active;
    }

    Date getTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        return cal.getTime();
    }

    int getId() {
        return id;
    }

    public int getActive() {
        return active;
    }
}
