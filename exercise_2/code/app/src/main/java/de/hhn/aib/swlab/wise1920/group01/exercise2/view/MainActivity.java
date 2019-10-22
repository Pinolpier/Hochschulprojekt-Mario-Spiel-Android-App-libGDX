package de.hhn.aib.swlab.wise1920.group01.exercise2.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

import de.hhn.aib.swlab.wise1920.group01.exercise2.R;
import de.hhn.aib.swlab.wise1920.group01.exercise2.controller.SyncService;
import de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.LoginProcessedInterface;
import de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.RegistrationProcessedInterface;

public class MainActivity extends AppCompatActivity {

    private EditText textInputUsername;
    private EditText textInputPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        testWebserviceImplementation();

//        textInputUsername = findViewById(R.id.editText_User);
//        textInputPassword = findViewById(R.id.editText_password);
//        Button buttonLogin = findViewById(R.id.button_login);
//        buttonLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String username = textInputUsername.getText().toString();
//                String password = textInputPassword.getText().toString();
//                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
//                startActivity(intent);
//            }
//        });
//        Button buttonRegister = findViewById(R.id.button_register);
//        buttonRegister.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(getApplicationContext(),"User: "+textInputUsername.getText().toString()+" Password: "+textInputPassword.getText().toString(),Toast.LENGTH_SHORT).show();
//                String username = textInputUsername.getText().toString();
//                String password = textInputPassword.getText().toString();
//            }
//        });
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

    public void testWebserviceImplementation() {
        final SyncService sync = new SyncService(this);
        sync.register("group01_dummy9", "group01_dummy9_password", null, new RegistrationProcessedInterface() {

            @Override
            public void onSuccess(String username, String password) {
                sync.login(username, password, new LoginProcessedInterface() {
                    @Override
                    public void onSuccess() {
                        sync.sendLocation(0.0, 0.0);
                        sync.changePrivacyRadius(9999990);
                        sync.changeDescription("This is user9 description.");
                        Log.wtf("TestWebserviceImplementation: ", Arrays.toString(sync.getUsersAround(999999999)));
                    }

                    @Override
                    public void onFailure() {
                        Log.wtf("Main Activity: ", "Login onFailure has been called");
                    }
                });
            }

            @Override
            public void onFailure() {
                Log.wtf("Main Activity: ", "Register onFailure has been called");
            }
        });
    }
}