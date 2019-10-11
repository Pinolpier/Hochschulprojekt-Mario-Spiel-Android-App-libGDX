package de.hhn.aib.swlab.wise1920.group01.exercise1;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;

public class TimerRepository {
    private static TimerDao mTimerDao;
    private LiveData<List<Timer>> mAllTimer;

    TimerRepository(Context context) {
        AppDatabase db = AppDatabase.getDatabase(context);
        mTimerDao = db.timerDao();
        mAllTimer = mTimerDao.getAllTimer();
    }

    public LiveData<List<Timer>> getAllTimer() {
        return mTimerDao.getAllTimer();
    }

    public long insert(Timer timer) {
        Log.e("Repository", "insertCalled");
        return mTimerDao.insert(timer);
    }

    public static void update(Timer timer) {
        Log.e("Reopsitory", "updaeCalled");
        mTimerDao.update(timer);
    }

    public static void delete(Timer timer) {
        mTimerDao.delete(timer);
    }
}
