package de.hhn.aib.swlab.wise1920.group01.exercise1;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TimerDao {
    @Query("SELECT * FROM timer WHERE active = :active")
    List<Timer> loadAllByActive (boolean active);

    @Insert
    void insertTimer(Timer timer);

    @Delete
    void deleteTimer(Timer timer);
}
