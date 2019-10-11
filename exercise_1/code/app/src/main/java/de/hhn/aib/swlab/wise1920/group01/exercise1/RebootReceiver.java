package de.hhn.aib.swlab.wise1920.group01.exercise1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RebootReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Intent serviceIntent = new Intent(context,ServiceClass.class);
        context.startService(serviceIntent);
    }
}
