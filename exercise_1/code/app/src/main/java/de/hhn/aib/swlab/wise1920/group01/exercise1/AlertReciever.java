package de.hhn.aib.swlab.wise1920.group01.exercise1;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

public class AlertReciever extends BroadcastReceiver {
    Intent intentAlarm;
    LiveData<List<Timer>> liveDataTimerList;
    List<Timer> timerList;
    AlarmHelper helper;
    AlarmManager manager;

    @Override
    public void onReceive(Context context, Intent intent) {

        //Aktion die beim Booten des Gerätes ausgeführt werdem sollen, wie etwa Alarme neu zu setzen
        if("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())){
            Toast.makeText(context, "Prozess gestartet", Toast.LENGTH_LONG).show();
            manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            helper = new AlarmHelper(context, manager);
            TimerDao timerDao = AppDatabase.getDatabase(context).timerDao();
            //liveDataTimerList = new TimerRepository(context.getApplicationContext()).getAllTimer();
            //timerList = liveDataTimerList.getValue();
            timerList = timerDao.getAllActiveTimers();
            if (timerList != null) {
                Iterator<Timer> iter = timerList.iterator();
                while (iter.hasNext()) {
                    Timer timer = iter.next();
                    if (timer.isActive()) {
                        Calendar cal = Calendar.getInstance();
                        cal.setTimeInMillis(timer.getTime());
                        helper.setAlarm(cal, timer.getId());
                        Log.e("Alarm Receiver: ", "Timer at " + timer.getTime() + " has been set!");
                    } else {
                        Log.e("Alarm Receiver: ", "Didn't trigger timer at " + timer.getTime() + "because it's inactive!");
                    }
                }
            } else {
                Toast.makeText(context, "List is null", Toast.LENGTH_LONG).show();
            }

        }

        //Aktion die beim erreichen eines Alarmzeitpunktes ausgeführt werden sollen
        else{

       //   Toast.makeText(context,"Recieved!!",Toast.LENGTH_LONG).show();
            intentAlarm = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intentAlarm.setClass(context,OverlayActivity.class);
            intentAlarm.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intentAlarm);
        }
    }
}
