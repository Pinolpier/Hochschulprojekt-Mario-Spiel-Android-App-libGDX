package com.mygdx.game;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import server.LoginProcessedInterface;
import server.RegistrationProcessedInterface;
import server.UserService;
import server.WebSocketService;

public class LoginActivity extends Activity {
    private EditText textInputUsername;
    private EditText textInputPassword;

    private UserService userService;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_login);

        userService = new UserService(this);

        textInputUsername = findViewById(R.id.textViewUsername);
        textInputPassword = findViewById(R.id.textViewPassword);


        Button buttonlogin = findViewById(R.id.button_login);
        Button buttonregister = findViewById(R.id.button_register);

        buttonlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LoginActivity.this.getClass().getSimpleName(), "Login button clicked!");
                String username = textInputUsername.getText().toString();
                String password = textInputPassword.getText().toString();
                login(username, password);
            }
        });

        buttonregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LoginActivity.this.getClass().getSimpleName(), "Register button clicked!");
                String username = textInputUsername.getText().toString();
                String password = textInputPassword.getText().toString();

                userService.register(username, password, new RegistrationProcessedInterface() {
                    @Override
                    public void onSuccess(String username, String password) {
                        login(username, password);
                    }

                    @Override
                    public void onFailure() {
                        Toast.makeText(context, R.string.registrationFailedToastMessage, Toast.LENGTH_LONG).show();
                        Log.wtf(LoginActivity.this.getClass().getSimpleName(), "Registration failed. RegistrationProcessedInterface.onFailure() has been called.");
                    }
                });
            }
        });
    }

    private void login(final String username, final String password) {
        Log.d(LoginActivity.this.getClass().getSimpleName(), "Login method started!");
        if (username == null || password == null || username.equals("") || password.equals("")) {
            Toast.makeText(this, R.string.emptyCredentialsToastMessage, Toast.LENGTH_LONG).show();
            return;
        }
        userService.login(username, password, new LoginProcessedInterface() {
            @Override
            public void onSuccess(String auth) {
                Log.d(LoginActivity.this.getClass().getSimpleName(), "LoginProcessedInterface.onSuccess() has been called. Login was successful.");
                //now that the game really starts start up the service and don't stop it until the Exit button on HomeActivity has been pressed.
                Intent serviceIntent = new Intent(LoginActivity.this, WebSocketService.class);
                Bundle bundle = new Bundle();
                bundle.putString("auth", auth);
                bundle.putString("username", username);
                bundle.putString("password", password);
                serviceIntent.putExtras(bundle);
                startService(serviceIntent);

                Intent intent = new Intent(context, HomeActivity.class);
                bundle.putBoolean("Sound", true);
                intent.putExtras(bundle);
                startActivity(intent);
            }

            @Override
            public void onFailure() {
                Log.wtf(LoginActivity.this.getClass().getSimpleName(), "LoginProcessedInterface.onFailure() has been called. Login failed!");
            }
        });
    }
}