package de.hhn.aib.swlab.wise1920.group01.exercise1;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class NotificationApp extends Application
{
    public static final String CHANNEL_ID = "alarmNotificationChannel";

    @Override
    public void onCreate()
    {
        super.onCreate();
        createNotificationChannel();
    }

    /**
     * This method is called by the onCreate method above and creates a Notification Channel for the NotificationServiceClass. It also turns off vibrations and
     * sounds when the Notification pops up.
     */
    public void createNotificationChannel()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel alarmChannel = new NotificationChannel(CHANNEL_ID,"Alarm Notification Channel", NotificationManager.IMPORTANCE_LOW);
            alarmChannel.setVibrationPattern(new long[]{ 0 });
            alarmChannel.enableVibration(true);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(alarmChannel);
        }
    }
}

