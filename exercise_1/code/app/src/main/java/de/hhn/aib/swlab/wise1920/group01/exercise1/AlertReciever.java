package de.hhn.aib.swlab.wise1920.group01.exercise1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.view.WindowManager;
import androidx.core.app.NotificationCompat;

public class AlertReciever extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification();
        notificationHelper.getManager().notify(1, nb.build());
        MediaPlayer media;
        // AlarmHelper helper = new AlarmHelper();
        // helper.showDialog();
/*         MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.coin);

          mediaPlayer.setLooping(true);
          mediaPlayer.start();

            MediaPlayer mp = new MediaPlayer();
            mp.setAudioStreamType(AudioManager.STREAM_ALARM);
        try {
            mp.setDataSource(context, Uri.parse("android.resource://PACKAGE_NAME/" + R.raw.coin));
            mp.setLooping(true);
            mp.prepare();
            mp.start();

        } catch (IOException e) {
            System.out.println(e);
        }*/
        media = MainActivity.getInstance().getMediaPlayer();
        media.create(context,R.raw.coin);
        media.setLooping(true);
        media.start();
        MainActivity.getInstance().startOverlay();

    }
}
