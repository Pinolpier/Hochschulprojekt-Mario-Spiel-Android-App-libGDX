package de.hhn.aib.swlab.wise1920.group01.exercise1;

import androidx.appcompat.app.AppCompatActivity;

public class AlarmHelper extends AppCompatActivity {

    public void showDialog(){
        AlertPopUp popUp = new AlertPopUp();
        popUp.show(getSupportFragmentManager(),"ersterTest");
    }
}
