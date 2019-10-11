package de.hhn.aib.swlab.wise1920.group01.exercise1;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import java.io.IOException;

public class OverlayActivity extends AppCompatActivity {
    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 5469;
    private Button beendenButton;
    private MediaPlayer mediaPlayer;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        path = "android.resource://de.hhn.aib.swlab.wise1920.group01.exercise1/raw/";
        path += getRingtonePath(this);
        Log.e("OverlayActivity:", "The path where a ringtone is searched is: " + path);
        setContentView(R.layout.activity_overlay);

        //Stack Overflow
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FIRST_APPLICATION_WINDOW|
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);


        //
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build());
        try {
            mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(path));
            mediaPlayer.prepare();
            Log.e("Overlay Activity:", "Alarm would start now!");
            //mediaPlayer.start();
            //mediaPlayer.setLooping(true);
        }
        catch(IOException e){
            e.printStackTrace();
        }

        beendenButton = findViewById(R.id.button_beenden);
        beendenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                finish();
            }});

    }

    public String getRingtonePath(Context context) {
        return new PreferenceManager(context).getDefaultSharedPreferences(context).getString("ringtone", "bowserlaugh");
    }
}