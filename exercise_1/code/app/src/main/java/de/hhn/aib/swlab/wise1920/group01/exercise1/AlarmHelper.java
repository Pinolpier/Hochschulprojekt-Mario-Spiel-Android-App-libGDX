package de.hhn.aib.swlab.wise1920.group01.exercise1;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

import androidx.appcompat.app.AppCompatActivity;

public class AlarmHelper extends AppCompatActivity {

    //AlarmManager alarmManager;

    public AlarmHelper(AlarmManager alarmManager){
   // this.alarmManager = alarmManager;
    }

    public void showDialog(){
        AlertPopUp popUp = new AlertPopUp();
        popUp.show(getSupportFragmentManager(),"ersterTest");
    }

    public void setAlarm(Calendar calendar, AlarmManager alarmManager){
       // alarmManager =(AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this,AlertReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,1,intent,0);
        if(calendar.before(Calendar.getInstance())){
            calendar.add(Calendar.DATE,1);
        }
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
    }

    public void cancelAlarm(AlarmManager alarmManager){
      //  alarmManager =(AlarmManager) getSystemService(Context.ALARM_SERVICE);
       // AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this,AlertReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,1,intent,0);
        alarmManager.cancel(pendingIntent);
    }

}
