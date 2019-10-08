package de.hhn.aib.swlab.wise1920.group01.exercise1;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;

public class TimerRepository {
    private TimerDao mTimerDao;
    private LiveData<List<Timer>> mAllTimer;

    TimerRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mTimerDao = db.timerDao();
        mAllTimer = mTimerDao.getAllTimer();
    }

    LiveData<List<Timer>> getAllTimer() {
        return mTimerDao.getAllTimer();
    }

    public void insert(Timer timer) {
        Log.e("Repository", "insertCalled");
        mTimerDao.insert(timer);
    }
}
