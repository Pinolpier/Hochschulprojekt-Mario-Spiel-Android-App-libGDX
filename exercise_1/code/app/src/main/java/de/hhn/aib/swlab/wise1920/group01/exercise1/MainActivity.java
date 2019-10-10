package de.hhn.aib.swlab.wise1920.group01.exercise1;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
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
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;

import static de.hhn.aib.swlab.wise1920.group01.exercise1.OverlayActivity.ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE;

public class MainActivity extends AppCompatActivity implements TimePickerFragment.TimePickerListener {
    private TimerRepository mTimerRepository;
 //   private TimerViewModel mTimerViewModel;
 //   int id = 0;
  //  private String editTextInput;
    private RecyclerView rvTodos;
    private AlarmHelper alarmHelper;
    private AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
 //       mTimerViewModel = ViewModelProviders.of(this).get(TimerViewModel.class);
   //     editTextInput = "alarm active";
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
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
        mTimerRepository = new TimerRepository(getApplication());
        rvTodos = findViewById(R.id.rvTodos);
        rvTodos.setAdapter(new MyAdapter(mTimerRepository.getAllTimer()));

        rvTodos = findViewById(R.id.rvTodos);
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

              //  String input = editTextInput;
                Intent serviceIntent = new Intent(this, ServiceClass.class);
             //   serviceIntent.putExtra("inputExtra", input);
                startService(serviceIntent);
            }
            if(!switchtest.isChecked())
            {
                j++;
                if(j == childCount)
                {
                 //   String input = editTextInput;
                    Intent serviceIntent = new Intent(this, ServiceClass.class);
                //    serviceIntent.putExtra("inputExtra", input);
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

        Toast.makeText(this,"Recieved "+ t.getId(),Toast.LENGTH_LONG).show(); //test zum anzeigen der ID
        alarmHelper.setAlarm(cal,t.getId());
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                // You don't have permission
                checkPermission();
            } else {
                // Do as per your logic
            }

        }

    }
    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            }
        }
    }
}
