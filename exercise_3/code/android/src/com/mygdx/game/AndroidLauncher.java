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

public class AndroidLauncher extends AndroidApplication implements MessageListener {

    boolean serviceBound = false;
    private MarioBros game;
    private AndroidApplicationConfiguration config;
    private Gson gson;
    private WebSocketService webSocketService;
    private String username, password, auth, gameID;
    private boolean gameHasBeenCreated = false;
    private boolean soundboolean;
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
        Log.d(AndroidLauncher.this.getClass().getSimpleName() + ":onCreate() ", "is running with username \"" + username + "\" and gameID \"" + gameID + "\"...");
        if (webSocketService == null) {
            Log.d(AndroidLauncher.this.getClass().getSimpleName(), "...and webSocketService == null so binding it");
            Intent serviceIntent = new Intent(this, WebSocketService.class);
            bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
            Log.d(this.getClass().getSimpleName(), "Bind Service should have been happened!");
        }
        soundboolean = getIntent().getExtras().getBoolean("soundonoff");
        game = new MarioBros(auth, username, password, gameID,soundboolean, new BackendCommunicator() {
            @Override
            public void sendMessage(GameMessage message) {
                if (webSocketService != null) {
                    webSocketService.sendMessage(gson.toJson(message));
                } else {
                    Log.wtf(AndroidLauncher.this.getClass().getSimpleName() + ":sendMessage() ", "webSocketService is null, probably while the game itself is tryinfg to send a message! Message to be sent is: " + gson.toJson(message));
                }
            }

            @Override
            public void stopGame(boolean sound) {
                killLibGDX(sound);
            }
        });
        gameHasBeenCreated = true;
        initialize(game, config);
    }

    private void killLibGDX(boolean sound) {
        gameHasBeenCreated = false;
        game = null;
        config = null;
        Intent homeIntent = new Intent(this, HomeActivity.class);
        Bundle extras = new Bundle();
        extras.putString("username", username);
        extras.putString("password", password);
        extras.putString("auth", auth);
        extras.putBoolean("Sound", sound);
        homeIntent.putExtras(extras);
        finish();
        startActivity(homeIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocketService != null) {
            webSocketService.deregisterListener(this);
            if (serviceBound) {
                try {
                    unbindService(connection);
                } catch (IllegalArgumentException iaex) {
                    Log.w(AndroidLauncher.this.getClass().getSimpleName(), "Can't unbind service because of an IllegalArgumentException - probably the Service is not bound for a strange reason of asynchronity.");
                }
            }
            webSocketService = null;
        }
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, R.string.cowardForbiddenToast, Toast.LENGTH_SHORT).show();
        Log.d(AndroidLauncher.this.getClass().getSimpleName() + ":back", "Back has been pressed. Ignoring in game and showing toast.");
    }

    @Override
    public void onMessageReceived(String message) {
        try {
            GameMessage msg = gson.fromJson(message, GameMessage.class);
            if (gameHasBeenCreated) {
                game.receiveMessage(msg);
            }
        } catch (JsonSyntaxException ex) {
            Log.w(this.getClass().getSimpleName(), "Couldn't cast message from backend, ignoring...\nMessage was: \"" + message + "\" printing stack trace...");
            ex.printStackTrace();
        }
    }
}