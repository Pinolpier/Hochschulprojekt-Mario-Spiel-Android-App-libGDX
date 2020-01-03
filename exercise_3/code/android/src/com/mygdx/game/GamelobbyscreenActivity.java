package com.mygdx.game;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class GamelobbyscreenActivity extends Activity {
    private ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gamelobbyscreen);
        listview = findViewById(R.id.lobbylistView);
    }
}
