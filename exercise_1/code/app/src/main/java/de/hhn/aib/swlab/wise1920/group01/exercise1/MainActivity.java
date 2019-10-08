package de.hhn.aib.swlab.wise1920.group01.exercise1;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements TimePickerFragment.TimePickerListener {
    private static MainActivity instance;
    private TodoRepository todoRepository;
    private AlarmHelper alarmHelper;
    private AlarmManager alarmManager;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;
        todoRepository = new TodoRepositoryInMemoryImpl();
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mediaPlayer = new MediaPlayer();
        alarmHelper = new AlarmHelper(this,alarmManager);
        Button button = findViewById(R.id.rvbtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePickerFragment = new TimePickerFragment();
                timePickerFragment.setCancelable(false);
                timePickerFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });

        RecyclerView rvTodos = findViewById(R.id.rvTodos);
        rvTodos.setAdapter(new MyAdapter(todoRepository));
        rvTodos.setLayoutManager(new LinearLayoutManager(this));

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        Todo todo = new Todo("Hours = " + hour + " Minutes = " + minute);
        todoRepository.addTodo(todo);
        Calendar timeCalender = Calendar.getInstance();
        timeCalender.set(Calendar.HOUR_OF_DAY,hour);
        timeCalender.set(Calendar.MINUTE,minute);
        timeCalender.set(Calendar.SECOND,0);

        alarmHelper.setAlarm(timeCalender);
    }

    public static MainActivity getInstance(){
        return instance;
    }

    public void startOverlay(){
        startActivity(new Intent(this,OverlayActivity.class));
    }

    public MediaPlayer getMediaPlayer(){
        return mediaPlayer;
    }

}

