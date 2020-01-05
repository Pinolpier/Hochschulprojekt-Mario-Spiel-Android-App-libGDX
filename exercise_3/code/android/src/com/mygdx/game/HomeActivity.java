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

import server.MessageListener;
import server.RegistrationProcessedInterface;
import server.UserService;
import server.WebSocketService;
import server.dtos.GameMessage;

public class HomeActivity extends Activity implements MessageListener {
    private WebSocketService webSocketService;
    private UserService userService;
    boolean serviceBound = false;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gson = new Gson();
        userService = new UserService(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);
        userService.register("gruppe1-Test", "gruppe1-Test", new RegistrationProcessedInterface() {
            @Override
            public void onSuccess(String username, String password) {

            }

            @Override
            public void onFailure() {
                Log.wtf("Test HomeActivity", "Registration failed obviously because hard coded credentials");
            }
        });
        Intent serviceIntent = new Intent(this, WebSocketService.class);
        bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
//        webSocketService.sendMessage(gson.toJson(new GameMessage("createGame", Double.toString(Math.round(Math.random() * 8999 + 1000)))));
//        webSocketService.sendMessage(gson.toJson(new GameMessage("getGameList", "")));
    }

    public void exitApp(View v) {
        finish();
        System.exit(0);
    }

    public void startGame(View v) {
        Intent homeIntent = new Intent(this, AndroidLauncher.class);
        startActivity(homeIntent);
        finish();
    }


    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            WebSocketService.WebSocketServiceBinder binder = (WebSocketService.WebSocketServiceBinder) service;
            webSocketService = binder.getService();
            serviceBound = true;
            webSocketService.registerListener(HomeActivity.this); //Setze diese Klasse als Listener fuer neue Nachrichten
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    @Override
    public void onMessageReceived(String message) {
        GameMessage msg = gson.fromJson(message, GameMessage.class);
//        switch (msg.getType()) {
//            case "ERROR":
//                Log.e(this.getClass().getSimpleName(), "Inhalt der Fehlermeldung des Servers: " + msg.getContent());
//                break;
//            case "ANSWER":
//                Log.i(this.getClass().getSimpleName(), "Inhalt der Antwort des Servers: " + msg.getContent());
//                break;
//            default:
//                Log.wtf(this.getClass().getSimpleName(), "Unbekannter Nachrichtentyp vom Server. Nachrichtentyp: \"" + msg.getType() + "\", Nachrichteninhalt: " + msg.getContent());
//                break;
//        }
    }
}