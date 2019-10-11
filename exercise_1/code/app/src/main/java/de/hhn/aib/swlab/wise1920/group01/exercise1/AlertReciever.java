package de.hhn.aib.swlab.wise1920.group01.exercise1;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

public class AlertReciever extends BroadcastReceiver {
    LiveData<List<Timer>> liveDataTimerList;
    List<Timer> timerList;
    AlarmHelper helper;
    AlarmManager manager;
    App app;

    @Override
    public void onReceive(Context context, Intent intent) {

        if("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())){
            app = new App();
            manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            helper = new AlarmHelper(context, manager);
            liveDataTimerList = new TimerRepository(context).getAllTimer();
            timerList = liveDataTimerList.getValue();
            Iterator<Timer> iter = timerList.iterator();
            while (iter.hasNext()) {
                Timer timer = iter.next();
                if (timer.isActive()) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(timer.getTime());
                    helper.setAlarm(cal);
//                    helper.setAlarm(cal, timer.getId());
                    Log.e("Alarm Receiver: ", "Timer at " + timer.getTime() + " has been set!");
                } else {
                    Log.e("Alarm Receiver: ", "Didn't trigger timer at " + timer.getTime() + "because it's inactive!");
                }
            }
        }
        else{
            MainActivity.getInstance().startOverlay();
        }
    }
}
