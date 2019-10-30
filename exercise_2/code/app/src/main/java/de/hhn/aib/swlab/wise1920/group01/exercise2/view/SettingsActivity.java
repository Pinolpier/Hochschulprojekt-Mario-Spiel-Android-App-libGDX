package de.hhn.aib.swlab.wise1920.group01.exercise2.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import de.hhn.aib.swlab.wise1920.group01.exercise2.R;

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
        Log.e("PreListener", "fired: " + key);
        if ("switch_poi".equals(key)) {
            Log.e("PrefChange", "" + sharedPreferences.getBoolean(key, false));
        } else if ("text_newpassword".equals(key)) {
            if (!sharedPreferences.getString(key, "").isEmpty())
                Log.e("PrefChange", "" + sharedPreferences.getString(key, ""));
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);
        }
    }
}
