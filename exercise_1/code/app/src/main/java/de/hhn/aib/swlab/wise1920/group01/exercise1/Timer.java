package de.hhn.aib.swlab.wise1920.group01.exercise1;


import android.util.Log;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Timer {

    Timer(Long time, boolean active) {
        setTime(time);
        setActive(active);
    }

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "time")
    private long time;

    @ColumnInfo(name = "active")
    private boolean active;

    int getId() {

        Log.e("getIdInTimer", String.valueOf(id));
        return id;
    }

    void setId(int id) {
        this.id = id;
    }

    long getTime() {
        return time;
    }

    void setTime(long time) {
        this.time = time;
    }

    boolean isActive() {
        return active;
    }

    void setActive(boolean active) {
        this.active = active;
    }
}
