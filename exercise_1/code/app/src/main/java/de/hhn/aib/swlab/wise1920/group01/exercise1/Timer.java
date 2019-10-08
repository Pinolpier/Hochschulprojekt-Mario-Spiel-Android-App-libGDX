package de.hhn.aib.swlab.wise1920.group01.exercise1;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Calendar;

@Entity
public class Timer {

    Timer(int id, Long time, boolean active) {
        setId(id);
        setTime(time);
        setActive(active);
    }

    @PrimaryKey
    int id;

    @ColumnInfo(name = "time")
    long time;

    @ColumnInfo(name = "active")
    public boolean active;

    public int getId() {
        return id;
    }

    private void setId(int id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    private void setTime(long time) {
        this.time = time;
    }

    public boolean isActive() {
        return active;
    }

    private void setActive(boolean active) {
        this.active = active;
    }
}
