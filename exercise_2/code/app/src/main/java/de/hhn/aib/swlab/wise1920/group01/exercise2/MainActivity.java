package de.hhn.aib.swlab.wise1920.group01.exercise2;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText textInputUsername;
    private EditText textInputPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textInputUsername = findViewById(R.id.editText_User);
        textInputPassword = findViewById(R.id.editText_password);
        Button buttonLogin = findViewById(R.id.button_login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = textInputUsername.getText().toString();
                String password = textInputPassword.getText().toString();
                Intent intent = new Intent(MainActivity.this,MapsActivity.class);
                startActivity(intent);
            }
        });
        Button buttonRegister = findViewById(R.id.button_register);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"User: "+textInputUsername.getText().toString()+" Password: "+textInputPassword.getText().toString(),Toast.LENGTH_SHORT).show();
                String username = textInputUsername.getText().toString();
                String password = textInputPassword.getText().toString();
            }
        });
    }
}
