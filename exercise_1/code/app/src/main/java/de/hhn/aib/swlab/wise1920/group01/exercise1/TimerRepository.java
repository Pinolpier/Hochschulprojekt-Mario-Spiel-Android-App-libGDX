package de.hhn.aib.swlab.wise1920.group01.exercise1;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;

public class TimerRepository {
    private static TimerDao mTimerDao;
    private LiveData<List<Timer>> mAllTimer;

    TimerRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mTimerDao = db.timerDao();
        mAllTimer = mTimerDao.getAllTimer();
    }

    public static LiveData<List<Timer>> getAllTimer() {
        return mTimerDao.getAllTimer();
    }

    public static void insert(Timer timer) {
        Log.e("Repository", "insertCalled");
        mTimerDao.insert(timer);
    }

    public static void update(Timer timer) {
        Log.e("Reopsitory", "updaeCalled");
        mTimerDao.update(timer);
    }

    public static void delete(Timer timer) {
        mTimerDao.delete(timer);
    }
}
