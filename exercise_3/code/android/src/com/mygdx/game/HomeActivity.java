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
import android.view.Window;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import server.MessageListener;
import server.WebSocketService;
import server.dtos.GameMessage;

public class HomeActivity extends Activity implements MessageListener {
    private WebSocketService webSocketService;
    boolean serviceBound = false;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gson = new Gson();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);
        Intent serviceIntent = new Intent(this, WebSocketService.class);
        serviceIntent.putExtras(getIntent().getExtras());
        bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
        Log.e(this.getClass().getSimpleName(), "Bind Service should have been happend!");
    }

    public void exitApp(View v) {
        webSocketService.close(null, null);
        finish();
        System.exit(0);
    }

    public void startGame(View v) {
        Intent homeIntent = new Intent(this, AndroidLauncher.class);
        Bundle extras = getIntent().getExtras();
        String gameID = extras.getString("username");
        extras.putString("gameID", gameID);
        homeIntent.putExtras(extras);
        startActivity(homeIntent);
        finish();
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            WebSocketService.WebSocketServiceBinder binder = (WebSocketService.WebSocketServiceBinder) service;
            webSocketService = binder.getService();
            Log.e("Home Activity: ", "Ist wsService null?" + (webSocketService == null));
            serviceBound = true;
            webSocketService.registerListener(HomeActivity.this); //Setze diese Klasse als Listener fuer neue Nachrichten
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    public void joinGame(View v) {
        Intent lobbyIntent = new Intent(this, GamelobbyscreenActivity.class);
        lobbyIntent.putExtras(getIntent().getExtras());
        startActivity(lobbyIntent);
    }

    @Override
    public void onMessageReceived(String message) {
        try {
            //TODO find out why HomeActivity receives messages, probably remove this class as listener for messages
            GameMessage msg = gson.fromJson(message, GameMessage.class);
        } catch (JsonSyntaxException ex) {
            Log.w(this.getClass().getSimpleName(), "Couldn't cast message from backend, ignoring...\nMessage was: \"" + message + "\" printing stack trace...");
            ex.printStackTrace();
        }
    }
}