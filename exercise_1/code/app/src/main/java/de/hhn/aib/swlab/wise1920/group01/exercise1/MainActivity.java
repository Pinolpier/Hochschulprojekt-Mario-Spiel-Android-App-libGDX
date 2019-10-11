package de.hhn.aib.swlab.wise1920.group01.exercise1;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements TimePickerFragment.TimePickerListener {
    private TimerRepository mTimerRepository;
    private TimerViewModel mTimerViewModel;
    int id = 0;
    private static MainActivity instance;
    //    private TodoRepository todoRepository;
    private String editTextInput;
    private RecyclerView rvTodos;
    private AlarmHelper alarmHelper;
    private AlarmManager alarmManager;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTimerViewModel = ViewModelProviders.of(this).get(TimerViewModel.class);

        editTextInput = "alarm active";
        instance = this;
//        todoRepository = new TodoRepositoryInMemoryImpl();
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
        mTimerRepository = new TimerRepository(this);
        rvTodos = findViewById(R.id.rvTodos);
        rvTodos.setAdapter(new MyAdapter(mTimerRepository.getAllTimer()));

        rvTodos = findViewById(R.id.rvTodos);
//        rvTodos.setAdapter(new MyAdapter(todoRepository));
        rvTodos.setLayoutManager(new LinearLayoutManager(this));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }
        return true;
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
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Timer t = new Timer(cal.getTimeInMillis(), true);
//        Log.e("MainActivity","onTimeSetCalled");
        mTimerRepository.insert(t);
        //Log.e("id", String.valueOf(t.getId()));
        alarmHelper.setAlarm(cal);
    }

    public static MainActivity getInstance(){
        return instance;
    }
    public void startOverlay(){
        startActivity(new Intent(this,OverlayActivity.class));
    }

    //  public MediaPlayer getMediaPlayer(){
    //    return mediaPlayer;
    //}

}
