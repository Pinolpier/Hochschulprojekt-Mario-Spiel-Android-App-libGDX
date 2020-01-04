package com.mygdx.game;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import server.BackendCommunicator;
import server.MessageListener;
import server.WebSocketService;
import server.dtos.GameMessage;

public class AndroidLauncher extends AndroidApplication implements BackendCommunicator, MessageListener {

    boolean serviceBound = false;
    private MarioBros game;
    private AndroidApplicationConfiguration config;
    private Gson gson;
    private WebSocketService webSocketService;
    private String username, password, auth, gameID;
    private boolean gameHasBeenCreated = false;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            WebSocketService.WebSocketServiceBinder binder = (WebSocketService.WebSocketServiceBinder) service;
            webSocketService = binder.getService();
            Log.e(AndroidLauncher.this.getClass().getSimpleName(), "onServiceConnected called. Ist wsService null?" + (webSocketService == null));
            serviceBound = true;
            webSocketService.registerListener(AndroidLauncher.this); //Setze diese Klasse als Listener fuer neue Nachrichten
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(this.getClass().getSimpleName(), "onServiceDisconnected has been called! The service is now disconnected from AndroidLauncher");
            serviceBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        config = new AndroidApplicationConfiguration();
        config.useGLSurfaceView20API18 = true;
        config.useGyroscope = true;
        config.useCompass = true;
        username = getIntent().getExtras().getString("username");
        password = getIntent().getExtras().getString("password");
        auth = getIntent().getExtras().getString("auth");
        gameID = getIntent().getExtras().getString("gameID");
        gson = new Gson();
        Log.e(AndroidLauncher.this.getClass().getSimpleName(), "onCreate of Android Launcher is running...");
        if (webSocketService == null) {
            Log.e(AndroidLauncher.this.getClass().getSimpleName(), "...and webSocketService == null so binding it");
            Intent serviceIntent = new Intent(this, WebSocketService.class);
            serviceIntent.putExtras(getIntent().getExtras());
            bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
            Log.e(this.getClass().getSimpleName(), "Bind Service should have been happend!");
        }
        game = new MarioBros(new BackendCommunicator() {
            @Override
            public void sendMessage(GameMessage message) {
                webSocketService.sendMessage(gson.toJson(message));
            }
        });
        gameHasBeenCreated = true;
        initialize(game, config);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocketService != null) {
            webSocketService.deregisterListener(this);
            if (serviceBound) {
                webSocketService.unbindService(connection);
            }
            webSocketService = null;
        }
    }

    //
//    protected void onPause() {
//        super.onPause();
//        if (webSocketService != null) {
//            webSocketService.deregisterListener(this);
//            if (serviceBound) {
//                webSocketService.unbindService(connection);
//            }
//            webSocketService = null;
//        }
//    }

    //    protected void onStop() {
//        super.onStop();
//        if (webSocketService != null) {
//            webSocketService.deregisterListener(this);
//            if (serviceBound) {
//                webSocketService.unbindService(connection);
//            }
//            webSocketService = null;
//        }
//    }
//
//    protected void onResume() {
//        if (gameHasBeenCreated) {
//            super.onResume();
//        }
//        if (webSocketService == null) {
//            Intent serviceIntent = new Intent(this, WebSocketService.class);
//            serviceIntent.putExtras(getIntent().getExtras());
//            bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
//            Log.e(this.getClass().getSimpleName(), "Bind Service should have been happend!");
//        } else {
//            Log.wtf("GamelobbyscreenActivity", "onResume has been called but webSocketService was not null");
//        }
//    }

    @Override
    public void sendMessage(GameMessage message) {
        if (webSocketService != null) {
            webSocketService.sendMessage(gson.toJson(message));
        } else {
            Log.wtf(AndroidLauncher.this.getClass().getSimpleName(), "webSocketService is null - can't send messages, multiplayer impossible");
            Toast.makeText(this, "Lost connection, please restart App", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onMessageReceived(String message) {
        try {
            GameMessage msg = gson.fromJson(message, GameMessage.class);
            if (gameHasBeenCreated) {
                game.receiveMessage(msg);
            } else {
                Log.e(AndroidLauncher.this.getClass().getSimpleName(), "Received message while game was not created! Message clear is: " + message);
            }
        } catch (JsonSyntaxException ex) {
            Log.w(this.getClass().getSimpleName(), "Couldn't cast message from backend, ignoring...\nMessage was: \"" + message + "\" printing stack trace...");
            ex.printStackTrace();
        }
    }
}