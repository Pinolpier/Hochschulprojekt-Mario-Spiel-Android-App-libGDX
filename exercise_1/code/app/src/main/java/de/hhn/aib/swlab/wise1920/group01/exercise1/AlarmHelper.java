package de.hhn.aib.swlab.wise1920.group01.exercise1;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import java.util.Calendar;


public class AlarmHelper{

    AlarmManager alarmManager;
    Context context;
    public AlarmHelper(Context context,AlarmManager alarmManager){
        this.context = context;
        this.alarmManager = alarmManager;
    }

    public void setAlarm(Calendar calendar){
        Intent intent = new Intent(context,AlertReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,1,intent,0);
        if(calendar.before(Calendar.getInstance())){
            calendar.add(Calendar.DATE,1);
        }
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
    }

    public void cancelAlarm(AlarmManager alarmManager){
        Intent intent = new Intent(context,AlertReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,1,intent,0);
        alarmManager.cancel(pendingIntent);
    }

}
