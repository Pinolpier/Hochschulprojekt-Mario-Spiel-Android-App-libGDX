package de.hhn.aib.swlab.wise1920.group01.exercise2.view;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import de.hhn.aib.swlab.wise1920.group01.exercise2.R;

/**
 * This class represents the Settingsview where the User can change his privacyradius, password, description,
 * and adjust whether pois, fuel prices and what locationhistory gets shown on the map.
 */
public class SettingsActivity extends AppCompatActivity {

    /**
     * This method is called from a call of the system method startActivity(Intent) only. It starts
     * the activity and loads the preferences from the preferences file.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);
        }
    }
}
