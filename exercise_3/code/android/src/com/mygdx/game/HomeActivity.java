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
import android.widget.Switch;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import server.MessageListener;
import server.WebSocketService;
import server.dtos.GameMessage;

public class HomeActivity extends Activity implements MessageListener {
    private WebSocketService webSocketService;
    boolean serviceBound = false;
    private String username, password, auth, gameID;
    private Gson gson;
    private Boolean soundboolean = true;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            WebSocketService.WebSocketServiceBinder binder = (WebSocketService.WebSocketServiceBinder) service;
            webSocketService = binder.getService();
            Log.d(HomeActivity.this.getClass().getSimpleName(), ":onServiceConnected()  The service is now connected to HomeActivity and webSocketService is null?: " + (webSocketService == null));
            serviceBound = true;
            webSocketService.registerListener(HomeActivity.this); //Setze diese Klasse als Listener fuer neue Nachrichten
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(HomeActivity.this.getClass().getSimpleName(), ":onServiceDisconnected()  The service is now disconnected from HomeActivity");
            serviceBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gson = new Gson();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);
        Intent serviceIntent = new Intent(this, WebSocketService.class);
        if (webSocketService == null) {
            bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
            Log.d(HomeActivity.this.getClass().getSimpleName() + ":onCreate() ", "Bind Service should have been happened!");
        } else {
            Log.d(HomeActivity.this.getClass().getSimpleName() + ":onCreate() ", "WebSocketService was not null, so service has not been bound again.");
        }

        final Switch soundswitch = findViewById(R.id.switch_soundonoff);
        soundswitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundboolean = !soundswitch.isChecked();
            }
        });
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
                    Log.w(HomeActivity.this.getClass().getSimpleName(), "Can't unbind service because of an IllegalArgumentException - probably the Service is not bound for a strange reason of asynchronity.");
                }
            }
            webSocketService = null;
            Log.d(HomeActivity.this.getClass().getSimpleName() + ":onDestroy() ", "Service has been unbound and has been Stopped!");
        }
    }

    public void startGame(View v) {
        username = getIntent().getExtras().getString("username");
        password = getIntent().getExtras().getString("password");
        auth = getIntent().getExtras().getString("auth");
        gameID = username;
        Log.d(HomeActivity.this.getClass().getSimpleName(), "Start Game has been pressed. Will now send join request for game with gameID as own username that is: " + username);
        if (serviceBound) {
            webSocketService.sendMessage(gson.toJson(new GameMessage("JOIN_GAME", auth, GameMessage.Status.OK, gameID, null)));
        } else {
            Log.e(HomeActivity.this.getClass().getSimpleName(), "Couldn't send Join request because service is not bound. Will show error toast, retry should work soon!");
            Toast.makeText(HomeActivity.this, R.string.cantSendJoinRequestToastMessage, Toast.LENGTH_LONG).show();
        }
        //finish();
    }

    public void exitApp(View v) {
        stopService(new Intent(this, WebSocketService.class));
        finish();
        System.exit(0);
    }

    public void joinGame(View v) {
        Intent lobbyIntent = new Intent(this, GamelobbyscreenActivity.class);
        Bundle extras = getIntent().getExtras();
        extras.putBoolean("soundonoff",soundboolean);
        lobbyIntent.putExtras(extras);
        startActivity(lobbyIntent);
        finish();
    }

    @Override
    public void onMessageReceived(String message) {
        try {
            GameMessage msg = gson.fromJson(message, GameMessage.class);
            if (msg.getType() != null && msg.getType().equals("JoinAnswer")) {
                if (msg.getStatus() == GameMessage.Status.OK) {
                    Intent gameIntent = new Intent(this, AndroidLauncher.class);
                    Bundle extras = getIntent().getExtras();
                    extras.putString("gameID", gameID);
                    extras.putBoolean("soundonoff",soundboolean);
                    gameIntent.putExtras(extras);
                    finish();
                    startActivity(gameIntent);
                } else {
                    Log.wtf(HomeActivity.this.getClass().getSimpleName(), "Can't join game with gameID own username! Game should be created! Potentially error occurs because two users are logged in using same username?");
                    Toast.makeText(HomeActivity.this, R.string.cantJoinOwnGame, Toast.LENGTH_LONG).show();
                }
            }
        } catch (JsonSyntaxException ex) {
            Log.w(this.getClass().getSimpleName(), "Couldn't cast message from backend, ignoring...\nMessage was: \"" + message + "\" printing stack trace...");
            ex.printStackTrace();
        }
    }
}