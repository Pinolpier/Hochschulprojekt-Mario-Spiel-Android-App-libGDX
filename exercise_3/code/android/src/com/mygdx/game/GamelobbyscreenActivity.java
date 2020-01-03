package com.mygdx.game;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;

import server.MessageListener;
import server.WebSocketService;
import server.dtos.GameMessage;

public class GamelobbyscreenActivity extends Activity implements MessageListener {
    boolean serviceBound = false;
    private ArrayList<String> allGames;
    private WebSocketService webSocketService;
    private Gson gson;
    private RecyclerView recyclerView;
    private GamelobbyscreenAdapter adapter;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            WebSocketService.WebSocketServiceBinder binder = (WebSocketService.WebSocketServiceBinder) service;
            webSocketService = binder.getService();
            serviceBound = true;
            webSocketService.registerListener(GamelobbyscreenActivity.this); //Setze diese Klasse als Listener fuer neue Nachrichten
            requestAllGames();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gamelobbyscreen);

        allGames = new ArrayList<>();
        gson = new Gson();

        Intent serviceIntent = new Intent(this, WebSocketService.class);
        serviceIntent.putExtras(getIntent().getExtras());
        bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
        Log.e(this.getClass().getSimpleName(), "Bind Service should have been happend!");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Button button = findViewById(R.id.reloadbutton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestAllGames();
            }
        });
    }

    private void requestAllGames() {
        GameMessage gameMessage = new GameMessage("GetGames", getIntent().getExtras().getString("auth"), GameMessage.Status.OK, null, null);
        webSocketService.sendMessage(gson.toJson(gameMessage));
    }

    @Override
    public void onMessageReceived(String message) {
        try {
            GameMessage gameMessage = gson.fromJson(message, GameMessage.class);
            if (gameMessage != null && "GameList".equals(gameMessage.getType()) && gameMessage.getStatus() == GameMessage.Status.OK) {
                allGames.clear();
                allGames = gameMessage.getStringList(); //Eine Liste aller Spiele, die auf dem Server existieren und denen der Spieler beitreten kann!
                allGames.add("First item");
                allGames.add("Second item");
                allGames.add("Third item");
                adapter = new GamelobbyscreenAdapter(this, allGames);
                recyclerView.setAdapter(adapter);
            } else {
                Log.i(this.getClass().getSimpleName(), "Message was not of Type LoginAnswer - ignoring...");
            }
        } catch (JsonSyntaxException ex) {
            Log.w(this.getClass().getSimpleName(), "Couldn't cast message from backend, ignoring...\nMessage was: \"" + message + "\" printing stack trace...");
            ex.printStackTrace();
        }
    }
}