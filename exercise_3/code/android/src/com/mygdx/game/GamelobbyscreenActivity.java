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
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.Objects;

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
    private String username, password, auth, gameID;
    private Boolean soundboolean;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            WebSocketService.WebSocketServiceBinder binder = (WebSocketService.WebSocketServiceBinder) service;
            webSocketService = binder.getService();
            serviceBound = true;
            Log.e(GamelobbyscreenActivity.this.getClass().getSimpleName(), "Service should have been bound to GamelobbyscreenActivity now!");
            webSocketService.registerListener(GamelobbyscreenActivity.this); //Setze diese Klasse als Listener fuer neue Nachrichten
            requestAllGames();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(GamelobbyscreenActivity.this.getClass().getSimpleName(), "onServiceDisconnected has been called! The service is now disconnected from GamelobbyscreenActivity");
            serviceBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gamelobbyscreen);

        soundboolean = Objects.requireNonNull(getIntent().getExtras()).getBoolean("soundonoff");
        allGames = new ArrayList<>();
        gson = new Gson();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (webSocketService == null) {
            Intent serviceIntent = new Intent(this, WebSocketService.class);
            bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
            Log.d(GamelobbyscreenActivity.this.getClass().getSimpleName(), "Bind Service should have been happened!");
        }

        Button button = findViewById(R.id.reloadbutton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestAllGames();
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
                    Log.w(GamelobbyscreenActivity.this.getClass().getSimpleName(), "Can't unbind service because of an IllegalArgumentException - probably the Service is not bound for a strange reason of asynchronity.");
                }
            }
            webSocketService = null;
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(GamelobbyscreenActivity.this, HomeActivity.class).putExtras(Objects.requireNonNull(getIntent().getExtras())));
        finish();
    }

    private void requestAllGames() {
        if (serviceBound) {
            GameMessage gameMessage = new GameMessage(GameMessage.Type.GET_GAMES, Objects.requireNonNull(getIntent().getExtras()).getString("auth"), GameMessage.Status.OK, null, null);
            webSocketService.sendMessage(gson.toJson(gameMessage));
        }
    }

    @Override
    public void onMessageReceived(String message) {
        try {
            GameMessage gameMessage = gson.fromJson(message, GameMessage.class);
            if (gameMessage != null && gameMessage.getType() == GameMessage.Type.GAME_LIST && gameMessage.getStatus() == GameMessage.Status.OK) {
                allGames.clear();
                allGames = gameMessage.getStringList(); //Eine Liste aller Spiele, die auf dem Server existieren und denen der Spieler beitreten kann!
                Log.d(GamelobbyscreenActivity.this.getClass().getSimpleName(), "Updaten auf dem UI Thread der empfangenen Ergebnisse als nächstes...");
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        adapter = new GamelobbyscreenAdapter(GamelobbyscreenActivity.this, allGames, new JoingameInterface() {
                            @Override
                            public void joinGame(String gameID) {
                                Log.i(GamelobbyscreenActivity.this.getClass().getSimpleName(), "User clicked " + gameID + " trying to join now!");
                                username = Objects.requireNonNull(getIntent().getExtras()).getString("username");
                                password = getIntent().getExtras().getString("password");
                                auth = getIntent().getExtras().getString("auth");
                                GamelobbyscreenActivity.this.gameID = gameID;
                                if (serviceBound) {
                                    webSocketService.sendMessage(gson.toJson(new GameMessage(GameMessage.Type.JOIN_GAME, auth, GameMessage.Status.OK, gameID, null)));
                                } else {
                                    Log.e(GamelobbyscreenActivity.this.getClass().getSimpleName(), "Couldn't send Join request because service is not bound. Will show error toast, retry should work soon!");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(GamelobbyscreenActivity.this, R.string.cantSendJoinRequestToastMessage, Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }
                        });
                        recyclerView.setAdapter(adapter);
                    }
                });
            } else if (gameMessage != null && gameMessage.getType() == GameMessage.Type.JOIN_ANSWER) {
                if (gameMessage.getStatus() == GameMessage.Status.OK) {
                    Intent gameIntent = new Intent(this, AndroidLauncher.class);
                    Bundle extras = getIntent().getExtras();
                    assert extras != null;
                    extras.putString("gameID", gameID);
                    extras.putBoolean("soundonoff",soundboolean);
                    extras.putInt("Level", gameMessage.getPayloadInteger());
                    gameIntent.putExtras(extras);
                    finish();
                    startActivity(gameIntent);
                } else {
                    Log.wtf(GamelobbyscreenActivity.this.getClass().getSimpleName(), "Can't join game with gameID " + gameID);
                    requestAllGames();
                    Toast.makeText(GamelobbyscreenActivity.this, R.string.cantJoinGame, Toast.LENGTH_LONG).show();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(GamelobbyscreenActivity.this, R.string.cantJoinGame, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        } catch (JsonSyntaxException ex) {
            Log.w(this.getClass().getSimpleName(), "Couldn't cast message from backend, ignoring...\nMessage was: \"" + message + "\" printing stack trace...");
            ex.printStackTrace();
        }
    }

}