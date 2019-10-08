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
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity implements TimePickerFragment.TimePickerListener {
    private static MainActivity instance;
    private TodoRepository todoRepository;
    private String editTextInput;
    private RecyclerView rvTodos;
    private AlarmHelper alarmHelper;
    private AlarmManager alarmManager;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextInput = "alarm active";
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

        rvTodos = findViewById(R.id.rvTodos);
        rvTodos.setAdapter(new MyAdapter(todoRepository));
        rvTodos.setLayoutManager(new LinearLayoutManager(this));
    }

    public void updateItems(View v)
    {
        int j = 0;
        for (int childCount = rvTodos.getChildCount(), i = 0; i < childCount; ++i)
        {
            final RecyclerView.ViewHolder holder = rvTodos.getChildViewHolder(rvTodos.getChildAt(i));
            Switch switchtest = holder.itemView.findViewById(R.id.switch1);
            if(switchtest.isChecked())
            {
                TextView txt = holder.itemView.findViewById(R.id.tv_description);
                System.out.println(txt.getText());

                String input = editTextInput;
                Intent serviceIntent = new Intent(this, ServiceClass.class);
                serviceIntent.putExtra("inputExtra", input);
                startService(serviceIntent);
            }
            if(!switchtest.isChecked())
            {
                j++;
                if(j == childCount)
                {
                    String input = editTextInput;
                    Intent serviceIntent = new Intent(this, ServiceClass.class);
                    serviceIntent.putExtra("inputExtra", input);
                    stopService(serviceIntent);
                }
            }
        }
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

    public String getRingtonePath(Context context) {
        return new PreferenceManager(context).getDefaultSharedPreferences(context).getString("ringtone", "bowserlaugh");
    }

    public void startOverlay(){
        startActivity(new Intent(this,OverlayActivity.class));
    }

    public MediaPlayer getMediaPlayer(){
        return mediaPlayer;
    }

}
