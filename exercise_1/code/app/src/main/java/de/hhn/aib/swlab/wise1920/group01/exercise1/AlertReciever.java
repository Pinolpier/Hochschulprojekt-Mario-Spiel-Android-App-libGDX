package de.hhn.aib.swlab.wise1920.group01.exercise1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.audiofx.Equalizer;
import android.provider.Settings;
import android.view.WindowManager;

public class AlertReciever extends BroadcastReceiver {
    Intent intentAlarm;

    @Override
    public void onReceive(Context context, Intent intent) {

        //Aktion die beim Booten des Gerätes ausgeführt werdem sollen, wie etwa Alarme neu zu setzen
        if("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())){



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
