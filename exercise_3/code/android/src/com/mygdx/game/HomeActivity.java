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

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            WebSocketService.WebSocketServiceBinder binder = (WebSocketService.WebSocketServiceBinder) service;
            webSocketService = binder.getService();
            Log.e("Home Activity: ", "Ist wsService null?: " + (webSocketService == null));
            serviceBound = true;
            webSocketService.registerListener(HomeActivity.this); //Setze diese Klasse als Listener fuer neue Nachrichten
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(this.getClass().getSimpleName(), "onServiceDisconnected has been called! The service is now disconnected from HomeActivity");
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
        serviceIntent.putExtras(getIntent().getExtras());
        startService(serviceIntent);
        if (webSocketService == null) {
            bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
            Log.e(HomeActivity.this.getClass().getSimpleName(), "Bind Service should have been happend!");
        }
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
            Intent serviceIntent = new Intent(this, WebSocketService.class);
            serviceIntent.putExtras(getIntent().getExtras());
            stopService(serviceIntent);
            Log.e(HomeActivity.this.getClass().getSimpleName(), "Service has been unbound in onDestroy and has been Stopped!");
        }
    }

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

//    protected void onResume() {
//        super.onResume();
//        if (webSocketService == null) {
//            Intent serviceIntent = new Intent(this, WebSocketService.class);
//            serviceIntent.putExtras(getIntent().getExtras());
//            bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
//            Log.e(this.getClass().getSimpleName(), "Bind Service should have been happend!");
//        } else {
//            Log.wtf("GamelobbyscreenActivity", "onResume has been called but webSocketService was not null");
//        }
//    }

    public void startGame(View v) {
        username = getIntent().getExtras().getString("username");
        password = getIntent().getExtras().getString("password");
        auth = getIntent().getExtras().getString("auth");
        gameID = username;
        Log.e(HomeActivity.this.getClass().getSimpleName(), "Start Game has been pressed. Will now send join request for game with gameID as own username that is: " + username);
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
        lobbyIntent.putExtras(getIntent().getExtras());
        startActivity(lobbyIntent);
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
                    gameIntent.putExtras(extras);
                    startActivity(gameIntent);
                } else {
                    Log.wtf(HomeActivity.this.getClass().getSimpleName(), "Can't join game with gameID own username! Game should be created! Potentially error occurs because two users are logged in using same username?");
                    Toast.makeText(HomeActivity.this, R.string.cantJoinOwnGame, Toast.LENGTH_LONG).show();
                }
            } else {
                Log.d(HomeActivity.this.getClass().getSimpleName(), "Message was not of type JoinAnswer - ignoring...");
            }
        } catch (JsonSyntaxException ex) {
            Log.w(this.getClass().getSimpleName(), "Couldn't cast message from backend, ignoring...\nMessage was: \"" + message + "\" printing stack trace...");
            ex.printStackTrace();
        }
    }
}