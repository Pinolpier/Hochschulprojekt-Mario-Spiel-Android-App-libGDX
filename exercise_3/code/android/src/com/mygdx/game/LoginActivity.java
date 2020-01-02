package com.mygdx.game;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends Activity {
    private EditText textInputUsername;
    private EditText textInputPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        textInputUsername = findViewById(R.id.textViewUsername);
        textInputPassword = findViewById(R.id.textViewPassword);


        Button buttonlogin = findViewById(R.id.button_login);
        Button buttonregister = findViewById(R.id.button_register);

       buttonlogin.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Log.d("LoginActivity: ", "Login button clicked!");
               String username = textInputUsername.getText().toString();
               String password = textInputPassword.getText().toString();
               //login(username, password);


               Intent homeIntent = new Intent(LoginActivity.this, HomeActivity.class);
               startActivity(homeIntent);
               finish();
           }
       });

       buttonregister.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Log.d("LoginActivity: ", "Register button clicked!");
               String username = textInputUsername.getText().toString();
               String password = textInputPassword.getText().toString();
               //...


               Intent homeIntent = new Intent(LoginActivity.this, HomeActivity.class);
               startActivity(homeIntent);
               finish();
           }
       });
    }
}
