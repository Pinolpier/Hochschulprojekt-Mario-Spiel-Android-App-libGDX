package com.mygdx.game;

import android.app.Activity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class GamelobbyscreenActivity extends Activity {
    private RecyclerView recyclerView;
    GamelobbyscreenAdapter adapter;
    ArrayList<String> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gamelobbyscreen);

        items = new ArrayList<>();
        items.add("First CardView item");
        items.add("Second CardView item");
        items.add("Third CardView item");
        items.add("Fourth CardView item");
        items.add("Fifth CardView item");
        items.add("Sixth CardView item");
        items.add("Seventh CardView item");
        items.add("Eigtht CardView item");


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GamelobbyscreenAdapter(this,items);
        recyclerView.setAdapter(adapter);
    }
}
