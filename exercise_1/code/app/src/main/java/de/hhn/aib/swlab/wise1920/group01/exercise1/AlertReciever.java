package de.hhn.aib.swlab.wise1920.group01.exercise1;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

public class AlertReciever extends BroadcastReceiver {
    private Intent intentAlarm;
    private List<Timer> timerList;
    private AlarmHelper helper;
    private AlarmManager manager;

    @Override
    public void onReceive(Context context, Intent intent) {
        manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        helper = new AlarmHelper(context, manager);
        TimerDao timerDao = AppDatabase.getDatabase(context).timerDao();
        timerList = timerDao.getAllActiveTimers();


        //Aktion die beim Booten des Gerätes ausgeführt werdem sollen, wie etwa Alarme neu zu setzen
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Iterator<Timer> iter = timerList.iterator();
            while (iter.hasNext()) {
                Timer timer = iter.next();
                if (timer.isActive()) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(timer.getTime());
                    helper.setAlarm(cal, timer.getId());
                    Intent serviceIntent = new Intent(context, NotificationServiceClass.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(serviceIntent);
                    } else {
                        context.startService(serviceIntent);
                    }
                }
            }
        }
        //Aktion die beim erreichen eines Alarmzeitpunktes ausgeführt werden sollen
        else {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            Timer time = timerDao.getTimerAt(c.getTimeInMillis());
            time.setActive(false);
            timerDao.update(time);
            intentAlarm = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intentAlarm.setClass(context, OverlayActivity.class);
            intentAlarm.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intentAlarm);
        }
    }
}