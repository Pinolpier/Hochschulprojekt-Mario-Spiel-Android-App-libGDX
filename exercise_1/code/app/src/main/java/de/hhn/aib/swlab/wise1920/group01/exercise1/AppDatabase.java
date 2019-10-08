package de.hhn.aib.swlab.wise1920.group01.exercise1;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Timer.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TimerDao timerDao();
}
