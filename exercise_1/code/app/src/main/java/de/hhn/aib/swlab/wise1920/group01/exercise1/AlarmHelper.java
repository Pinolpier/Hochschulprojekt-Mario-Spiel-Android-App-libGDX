package de.hhn.aib.swlab.wise1920.group01.exercise1;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import java.util.Calendar;


public class AlarmHelper{

    AlarmManager alarmManager;
    Context context;
    int id;
    public AlarmHelper(Context context,AlarmManager alarmManager){
        this.context = context;
        this.alarmManager = alarmManager;
        id = 0;
    }

    public void setAlarm(Calendar calendar, int id){
        Intent intent = new Intent(context,AlertReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,id,intent,0);
        if(calendar.before(Calendar.getInstance())){
            calendar.add(Calendar.DATE,1);
        }
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
    }

    /**
     * Methode um einen bereits gesetzten Alarm zu Löschen
     * @param
     */
    public void cancelAlarm(int id){
        Intent intent = new Intent(context,AlertReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,id,intent,0);
        alarmManager.cancel(pendingIntent);
    }

}
