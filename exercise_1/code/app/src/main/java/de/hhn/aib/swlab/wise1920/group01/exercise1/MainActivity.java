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

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;

import static de.hhn.aib.swlab.wise1920.group01.exercise1.OverlayActivity.ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE;

public class MainActivity extends AppCompatActivity implements TimePickerFragment.TimePickerListener {
    private TimerRepository mTimerRepository;
    private RecyclerView rvAlarms;
    private AlarmHelper alarmHelper;
    private TimerDao timerDao;

    /**
     * This method is only called by the Android system. It sets up the functionality of the main activity.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
        //initialize a reference to the system wide ALARM_SERVICE
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmHelper = new AlarmHelper(this, alarmManager);
        //initialize a reference to the apps database
        timerDao= AppDatabase.getDatabase(this).timerDao();
        mTimerRepository = new TimerRepository(this);
        rvAlarms = findViewById(R.id.rvAlarms);
        MyAdapter adapter = new MyAdapter(mTimerRepository.getAllTimer());
        rvAlarms.setAdapter(adapter);
        rvAlarms = findViewById(R.id.rvAlarms);
        rvAlarms.setLayoutManager(new LinearLayoutManager(this));

        //setup of the "+"-button to add a new alarm
        Button button = findViewById(R.id.rvbtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePickerFragment = new TimePickerFragment();
                timePickerFragment.setCancelable(false);
                timePickerFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });

        //Deleting the timer with a long click on the time
        adapter.setOnLongClickListener(new MyAdapter.OnLongClickListener() {
            @Override
            public void onLongClick(Timer timer) {
                alarmHelper.cancelAlarm(timer.getId());
                TimerRepository.delete(timer);
                updateItems(rvAlarms);
            }
        });
    }
    /**
     * This method is called when an alarm switch is changed (meaning the user activated or deactivated an alarm.
     * It also shows the "alarm active" Notification when at least one switch is active.
     * This method must not be called manually!
     *
     * @param v This method must not be called manually. Param is the view and is known to the android system.
     */
    public void updateItems(View v) {
        //iterate through all items in the RecyclerView
        for (int childCount = rvAlarms.getChildCount(), i = 0; i < childCount; ++i) {
            final RecyclerView.ViewHolder holder = rvAlarms.getChildViewHolder(rvAlarms.getChildAt(i));
            //get a reference to the switches and the textViews
            Switch switchtest = holder.itemView.findViewById(R.id.switch1);
            TextView alarmTimeTextView = holder.itemView.findViewById(R.id.tv_description);

            //Get the time from the textView and create a reference to a calendar object at that time
            String time = (String) alarmTimeTextView.getText();
            String[] timeArray = time.split(":");
            int hour = Integer.parseInt(timeArray[0]);
            int minute = Integer.parseInt(timeArray[1]);
            Calendar c = Calendar.getInstance();

            c.set(Calendar.HOUR_OF_DAY,hour);
            c.set(Calendar.MINUTE,minute);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);

            //Get the timer from the database to activate an alarm, if the switch has beeen changed to active ot deactivate it otherwise.
            Timer te = mTimerRepository.getTimerAt(c.getTimeInMillis());
            if(te != null) {
                if (switchtest.isChecked()) {
                    te.setActive(true);
                    timerDao.update(te);
                    alarmHelper.setAlarm(c, te.getId());
                }
                if (!switchtest.isChecked()) {
                    te.setActive(false);
                    timerDao.update(te);
                    alarmHelper.cancelAlarm(te.getId());
                }
            }
        }

        //The following is used to activate / deactivate the notification if the first / last alarm is activated / deactivated.
        if (mTimerRepository.getAllActiveTimers() == null || mTimerRepository.getAllActiveTimers().size() < 1) {
            //Deaktiveren
            Intent serviceIntent = new Intent(this, NotificationServiceClass.class);
            stopService(serviceIntent);
        } else {
            //Aktivieren
            Intent serviceIntent = new Intent(this, NotificationServiceClass.class);
            startService(serviceIntent);
        }
    }

    /**
     * This method is called when the users presses the 3 dots in the upper left to go to the settings.
     * This method is called by the android system and must not be called manually!
     *
     * @param menu the menu that is opend
     * @return always {@code true}
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * This method is called when the user chooses "Settings" in the Menu. It starts the settings activity.
     * This method is called by the android system and must not be called manually!
     * @param item the Settings MenuItem
     * @return always true
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }
        return true;
    }

    /**
     * This method is used to add an alarm when the users selected a time using the TimePicker
     * This method is called by the android system and must not be called manually!
     * @param timePicker the TimePicker used by the user
     * @param hour The choosen hour
     * @param minute The choosen minute
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Timer t = new Timer(cal.getTimeInMillis(), true);
        t.setId((int) mTimerRepository.insert(t));
        alarmHelper.setAlarm(cal, t.getId());
        updateItems(rvAlarms);
    }

    /**
     * This method is used to check whether an Android 10 permission to draw overlays is granted to the app. If not granted the app asks for the permission.
     */
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
