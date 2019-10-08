package de.hhn.aib.swlab.wise1920.group01.exercise1;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements TimePickerFragment.TimePickerListener {
    private TimerRepository mtimerRepository;
    private TimerViewModel mTimerViewModel;
    int id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTimerViewModel = ViewModelProviders.of(this).get(TimerViewModel.class);

        Button button = findViewById(R.id.rvbtn);
        //TextView tv_description = findViewById(R.id.tv_description);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePickerFragment = new TimePickerFragment();
                timePickerFragment.setCancelable(false);
                timePickerFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });
        mtimerRepository = new TimerRepository(getApplication());
        RecyclerView rvTodos = findViewById(R.id.rvTodos);
        rvTodos.setAdapter(new MyAdapter(mtimerRepository.getAllTimer()));
        rvTodos.setLayoutManager(new LinearLayoutManager(this));

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
        Log.e("MainACtivity","onTimeSetCalled");
        mtimerRepository.insert(t);

    }
}

