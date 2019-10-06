package de.hhn.aib.swlab.wise1920.group01.exercise1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class AlertReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //NotificationHelper notificationHelper = new NotificationHelper(context);
        //NotificationCompat.Builder nb = notificationHelper.getChannelNotification();
        //notificationHelper.getManager().notify(1, nb.build());
        AlarmHelper helper = new AlarmHelper();
        helper.showDialog();
    }
}
