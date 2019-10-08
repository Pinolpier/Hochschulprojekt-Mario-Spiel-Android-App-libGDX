package de.hhn.aib.swlab.wise1920.group01.exercise1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlertReciever extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        if("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())){

        }
        else{
            MainActivity.getInstance().startOverlay();
        }
    }
}
