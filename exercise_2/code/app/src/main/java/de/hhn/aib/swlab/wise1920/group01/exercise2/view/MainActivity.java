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

public class MainActivity extends AppCompatActivity {

    private EditText textInputUsername;
    private EditText textInputPassword;
    private AuthService auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        testWebserviceImplementation();

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