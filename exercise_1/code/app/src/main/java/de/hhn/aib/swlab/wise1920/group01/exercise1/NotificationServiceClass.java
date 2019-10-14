package de.hhn.aib.swlab.wise1920.group01.exercise1;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import static de.hhn.aib.swlab.wise1920.group01.exercise1.NotificationApp.CHANNEL_ID;

/**
 * Lots of code is from an online tutorial. Changed to work for our needs.
 */
public class NotificationServiceClass extends Service
{

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * This method is called by the system method startService(Intent,service). It sets up the persistent Notification and starts the Notificationservice.
     *
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String input = intent.getStringExtra("inputExtra");

        Intent notificationIntent = new Intent(this,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("alarm active").setContentText(input)
                .setSmallIcon(R.drawable.ic_access_alarms_black_24dp)
                .setContentIntent(pendingIntent).build();
        startForeground(1,notification);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
