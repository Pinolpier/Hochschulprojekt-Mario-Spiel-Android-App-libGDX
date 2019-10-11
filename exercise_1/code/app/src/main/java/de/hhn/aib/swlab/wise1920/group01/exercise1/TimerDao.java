package de.hhn.aib.swlab.wise1920.group01.exercise1;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TimerDao {
    @Insert
    long insert(Timer timer);

    @Query("DELETE FROM timer")
    void deleteAll();

    @Query("SELECT * from timer ORDER BY time ASC")
    LiveData<List<Timer>> getAllTimer();

    @Query("SELECT * FROM Timer where active=1")
    List<Timer> getAllActiveTimers();

    @Update
    void update(Timer timer);

    @Delete
    void delete(Timer timer);
}
