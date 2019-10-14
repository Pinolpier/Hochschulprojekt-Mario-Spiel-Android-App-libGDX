package de.hhn.aib.swlab.wise1920.group01.exercise1;

import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import java.io.IOException;

/**
 * Represents the Activity shown during alarm time and plays the alarm sound.
 */
public class OverlayActivity extends AppCompatActivity {
    public static final int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 5469;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String path = "android.resource://de.hhn.aib.swlab.wise1920.group01.exercise1/raw/";
        path += getRingtonePath(this);
        setContentView(R.layout.activity_overlay);

        //Von Stack Overflow welche Flags gesetzt werden müssen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FIRST_APPLICATION_WINDOW|
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build());
        try {
            mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(path));
            mediaPlayer.prepare();
            mediaPlayer.start();
            mediaPlayer.setLooping(true);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "File not found!", Toast.LENGTH_LONG).show();
        }

        Button beendenButton = findViewById(R.id.button_beenden);
        beendenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                TimerRepository mTimerRepository = new TimerRepository(v.getContext());
                if (mTimerRepository.getAllActiveTimers() == null || mTimerRepository.getAllActiveTimers().size() < 1) {
                    Intent serviceIntent = new Intent(v.getContext(), NotificationServiceClass.class);
                    stopService(serviceIntent);
                }
                finish();
            }});

    }

    /**
     * Returns the path to the ringtone to be played.
     *
     * @param context The apps context.
     * @return The path to the ringtone that has been setup in the settings or otherwise a default value.
     */
    private String getRingtonePath(Context context) {
        return new PreferenceManager(context).getDefaultSharedPreferences(context).getString("ringtone", "bowserlaugh");
    }
}