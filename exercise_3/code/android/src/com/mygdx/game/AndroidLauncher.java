package com.mygdx.game;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

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
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            WebSocketService.WebSocketServiceBinder binder = (WebSocketService.WebSocketServiceBinder) service;
            webSocketService = binder.getService();
            Log.e("Android Launcher: ", "Ist wsService null?" + (webSocketService == null));
            serviceBound = true;
            Log.e("Ist der scheiß Service", " verbunden und hat webSocketService einen Wert? serviceBound: " + serviceBound + " websocketService == null " + (webSocketService == null));
            webSocketService.registerListener(AndroidLauncher.this); //Setze diese Klasse als Listener fuer neue Nachrichten
            sendMessage(new GameMessage("JOIN_GAME", auth, GameMessage.Status.OK, gameID, null));
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        config = new AndroidApplicationConfiguration();
		config.useGLSurfaceView20API18=true;
		config.useGyroscope=true;
		config.useCompass=true;
        username = getIntent().getExtras().getString("username");
        password = getIntent().getExtras().getString("password");
        auth = getIntent().getExtras().getString("auth");
        gameID = getIntent().getExtras().getString("gameID");
        gson = new Gson();
        Intent serviceIntent = new Intent(this, WebSocketService.class);
        serviceIntent.putExtras(getIntent().getExtras());
        bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
        Log.e(this.getClass().getSimpleName(), "Bind Service should have been happend!");
        game = new MarioBros(new BackendCommunicator() {
            @Override
            public void sendMessage(GameMessage message) {
                webSocketService.sendMessage(gson.toJson(message));
            }
        });
    }

    @Override
    public void sendMessage(GameMessage message) {
        webSocketService.sendMessage(gson.toJson(message));
    }

    @Override
    public void onMessageReceived(String message) {
        try {
            GameMessage msg = gson.fromJson(message, GameMessage.class);
            if (msg.getType() != null && msg.getType().equals("JoinAnswer")) {
                if (msg.getStatus() == GameMessage.Status.OK) {
                    Log.d(this.getClass().getSimpleName(), "Got positive answer to join game with gameID " + gameID + " will initialize game now and start.");
                    initialize(game, config);
                } else {
                    Log.e(this.getClass().getSimpleName(), "can't join game. Probably game is full, gameID used for request was: " + gameID);
                    //TODO show hint (e.g. Toast) that game couldn't be joined and go back to GamelobbyscreenActivity or HomeActivity (maybe show toast there, problem right now is that no context is provided here)
                    //Toast.makeText(this, R.string.noJoinApprovalMessage + gameID);
                }
            } else {
                game.receiveMessage(msg);
            }
        } catch (JsonSyntaxException ex) {
            Log.w(this.getClass().getSimpleName(), "Couldn't cast message from backend, ignoring...\nMessage was: \"" + message + "\" printing stack trace...");
            ex.printStackTrace();
        }
    }
}