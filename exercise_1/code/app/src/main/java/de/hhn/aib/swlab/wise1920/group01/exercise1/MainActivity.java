package de.hhn.aib.swlab.wise1920.group01.exercise1;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;

import static de.hhn.aib.swlab.wise1920.group01.exercise1.OverlayActivity.ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE;

public class MainActivity extends AppCompatActivity implements TimePickerFragment.TimePickerListener {
    private TimerRepository mTimerRepository;
    private String editTextInput;
    private RecyclerView rvTodos;
    private AlarmHelper alarmHelper;
    private AlarmManager alarmManager;
    private TimerDao timerDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
        editTextInput = "alarm active";
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmHelper = new AlarmHelper(this, alarmManager);
        timerDao= AppDatabase.getDatabase(this).timerDao();
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
        MyAdapter adapter = new MyAdapter(mTimerRepository.getAllTimer());
        rvTodos.setAdapter(adapter);

        rvTodos = findViewById(R.id.rvTodos);
        rvTodos.setLayoutManager(new LinearLayoutManager(this));

        //Deleting the timer
        adapter.setOnLongClickListener(new MyAdapter.OnLongClickListener() {
            @Override
            public void onLongClick(Timer timer) {
                alarmHelper.cancelAlarm(timer.getId());
                TimerRepository.delete(timer);
                updateItems(rvTodos);
            }
        });
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

    public void updateItems(View v) {
        for (int childCount = rvTodos.getChildCount(), i = 0; i < childCount; ++i) {
            final RecyclerView.ViewHolder holder = rvTodos.getChildViewHolder(rvTodos.getChildAt(i));
            Switch switchtest = holder.itemView.findViewById(R.id.switch1);
            TextView txt = holder.itemView.findViewById(R.id.tv_description);

            String string = (String) txt.getText();
            String[] timedates = string.split(":");
            int hour = Integer.parseInt(timedates[0]);
            int minute = Integer.parseInt(timedates[1]);
            Calendar c = Calendar.getInstance();

            c.set(Calendar.HOUR_OF_DAY,hour);
            c.set(Calendar.MINUTE,minute);
            c.set(Calendar.SECOND,0);
            c.set(Calendar.MILLISECOND,0);
            Timer te = mTimerRepository.getTimerAt(c.getTimeInMillis());

            if (switchtest.isChecked()) {
                te.setActive(true);
                timerDao.update(te);
                alarmHelper.setAlarm(c,te.getId());
            }
            if (!switchtest.isChecked())
            {
                te.setActive(false);
                timerDao.update(te);
                alarmHelper.cancelAlarm(te.getId());
            }
        }
        if (mTimerRepository.getAllActiveTimers()==null){
            Intent serviceIntent = new Intent(this, ServiceClass.class);
            stopService(serviceIntent);
        }
        else if (mTimerRepository.getAllActiveTimers().size() < 1) {
            //Deaktivieren
            Intent serviceIntent = new Intent(this, ServiceClass.class);
            stopService(serviceIntent);
        } else {
            //Aktivieren
            Intent serviceIntent = new Intent(this, ServiceClass.class);
            startService(serviceIntent);
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
        t.setId((int) mTimerRepository.insert(t));
        //Log.e("id", String.valueOf(t.getId()));

        Toast.makeText(this, "Recieved " + t.getId(), Toast.LENGTH_LONG).show(); //test zum anzeigen der ID
        alarmHelper.setAlarm(cal, t.getId());
    }

    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            }
        }
    }

}
