package de.hhn.aib.swlab.wise1920.group01.exercise2.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import de.hhn.aib.swlab.wise1920.group01.exercise2.R;
import de.hhn.aib.swlab.wise1920.group01.exercise2.controller.SettingsFunction;

public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    /**
     * This method is called from a call of the system method startActivity(Intent) only. It starts
     * the activity and loads the preferences from the preferences file.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        PreferenceManager.setDefaultValues(this, R.xml.preferences, true);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        SettingsFunction sf = new SettingsFunction();
        switch (key) {
            case "list_radius":
                sf.changeRadius(sharedPreferences.getString(key, "-1"));
                break;
            case "list_interval":
                sf.changeInterval(sharedPreferences.getString(key, "1800"));
                break;
            case "switch_poi":
                sf.changePOI(sharedPreferences.getBoolean(key, false));
                break;
            case "switch_weatherdata":
                sf.changeWeatherdata(sharedPreferences.getBoolean(key, false));
                break;
            case "switch_locationhistory":
                sf.changeLocationHistory(sharedPreferences.getBoolean(key, false));
                break;
            case "list_locationhistorytimeframe":
                sf.changeLocationHistoryTimeframe(sharedPreferences.getString(key, "604800"));
                break;
            case "text_newpassword":
                if (!sharedPreferences.getString(key, "").isEmpty())
                    sf.changePassword(sharedPreferences.getString(key, ""));
                else
                    Toast.makeText(this, "Passwort darf nicht leer sein!", Toast.LENGTH_LONG).show();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + key);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);
        }
    }
}
