package de.hhn.aib.swlab.wise1920.group01.exercise1;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import java.util.Calendar;


 class AlarmHelper{

    private final AlarmManager alarmManager;
    private final Context context;

    AlarmHelper(Context context,AlarmManager alarmManager){
        this.context = context;
        this.alarmManager = alarmManager;
    }

    /**
     * Die Methode setzt einen Alarm zu bestimmter Uhrzeit und mit individueller ID
     * @param calendar calender objekt mit der Uhrzeit für den der Alarm gesetzt werden soll
     * @param id , des Timers, für den der Alarm gesetzt werden soll
     */
     void setAlarm(Calendar calendar, int id){
        Intent intent = new Intent(context,AlertReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,id,intent,0);
        if(calendar.before(Calendar.getInstance())){
            calendar.add(Calendar.DATE,1);
        }
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
    }

    /**
     * Methode um einen bereits gesetzten Alarm zu Löschen
     * @param  id des Timers, für welchen der Alarm deaktiviert werden soll
     */
      void cancelAlarm(int id){
        Intent intent = new Intent(context,AlertReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,id,intent,0);
        alarmManager.cancel(pendingIntent);
    }

}
