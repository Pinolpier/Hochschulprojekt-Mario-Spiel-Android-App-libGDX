package de.hhn.aib.swlab.wise1920.group01.exercise1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity implements TimePickerFragment.TimePickerListener {
    private TodoRepository todoRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        todoRepository = new TodoRepositoryInMemoryImpl();

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

        RecyclerView rvTodos = findViewById(R.id.rvTodos);
        rvTodos.setAdapter(new MyAdapter(todoRepository));
        rvTodos.setLayoutManager(new LinearLayoutManager(this));
        //startActivity(new Intent(this, SettingsActivity.class));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        Todo todo = new Todo("Hours = " + hour + " Minutes = " + minute);
        todoRepository.addTodo(todo);
        Log.d("onTimeSet", "time set");
    }

    public String getRingtonePath(Context context) {
        return new PreferenceManager(context).getDefaultSharedPreferences(context).getString("ringtone", "bowserlaugh");
    }
}

