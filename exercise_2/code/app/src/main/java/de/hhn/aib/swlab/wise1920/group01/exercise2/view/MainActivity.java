package de.hhn.aib.swlab.wise1920.group01.exercise2.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import de.hhn.aib.swlab.wise1920.group01.exercise2.R;
import de.hhn.aib.swlab.wise1920.group01.exercise2.controller.AuthService;
import de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.LoginProcessedInterface;
import de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.RegistrationProcessedInterface;

/**
 * This class represents the Login/Registrationscreen where the user can login with his username and
 * password or register a new account.
 */
public class MainActivity extends AppCompatActivity {

    private EditText textInputUsername;
    private EditText textInputPassword;
    private AuthService auth;

    /**
     * This method is called when the app is started and creates the Login/Registration View
     * for the user
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textInputUsername = findViewById(R.id.editText_User);
        textInputPassword = findViewById(R.id.editText_password);
        Button buttonLogin = findViewById(R.id.button_login);
        auth = new AuthService(this);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("MainActivity: ", "Login button clicked!");
                String username = textInputUsername.getText().toString();
                String password = textInputPassword.getText().toString();
                login(username, password);
            }
        });
        Button buttonRegister = findViewById(R.id.button_register);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("MainActivity: ", "Register button clicked!");
                String username = textInputUsername.getText().toString();
                String password = textInputPassword.getText().toString();
                auth.register(username, password, null, new RegistrationProcessedInterface() {

                    @Override
                    public void onSuccess(String username, String password) {
                        login(username, password);
                    }

                    @Override
                    public void onFailure() {
                        Log.wtf("Main Activity: ", "Registration onFailure has benn called!");
                    }
                });
            }
        });
    }

    /**
     * This method checks if the username and password input is correct by accessing the information
     * from the server and either starts the Mapsactivity to bring the user to the mapview on success
     * or shows that either the put in username or passowrd was incorrect on failure.
     * @param username      username input from user
     * @param password      password input from user
     */
    private void login(String username, String password) {
        Log.d("MainActivity: ", "Login method started!");
        auth.login(username, password, new LoginProcessedInterface() {
            @Override
            public void onSuccess() {
                Log.d("MainActivity: ", "Login on Success");
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("jwt", auth.getJWT());
                bundle.putString("id", auth.getUserID());
                bundle.putString("username", auth.getUsername());
                bundle.putString("description", auth.getDescription());
                bundle.putString("password", auth.getPassword());
                bundle.putDouble("privacy", auth.getPrivacyRadius());
                intent.putExtras(bundle);
                startActivity(intent);
            }

            @Override
            public void onFailure() {
                Log.wtf("Main Activity: ", "Login onFailure has been called");
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
}