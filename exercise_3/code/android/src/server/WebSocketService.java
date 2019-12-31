package server;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import server.dtos.GameMessage;

public class WebSocketService extends Service implements MessageListener {
    private static final String URL = "wss://g1-master.stud.ex3.swlab-hhn.de/ws"; //URL vom Backend
    //URL vorher: "wss://tst-simple-example.stud.ex3.swlab-hhn.de/ws"
    private final IBinder binder = new WebSocketServiceBinder();
    private OkHttpClient client;
    private WebSocket webSocket;
    private List<MessageListener> listeners;
    private String auth, username, password;
    private Gson gson;

    //Standard bound Service Sachen
    public class WebSocketServiceBinder extends Binder{
        public WebSocketService getService(){
            return WebSocketService.this;
        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if(webSocket==null){
            //Erzeuge neue Websocket Verbindung mit Backend
            Request request = new Request.Builder().url(URL).build();
            webSocket = client.newWebSocket(request,new SocketListener());
            Log.d("Service", "onBind called");
            Bundle extras = intent.getExtras();
            this.auth = extras.getString("auth");
            this.username = extras.getString("username");
            this.password = extras.getString("password");
            registerListener(this);
            login();
        }
        return binder;
    }

    @Override
    public void onCreate(){
        listeners = new ArrayList<>();
        client = new OkHttpClient();
        gson = new Gson();
        Log.d("Service", "OnCreate Executed");
    }

    /**
     * Sends a new message
     * @param message  the message to be send
     */
    public void sendMessage(String message) {
        if (webSocket != null) {
            boolean status = webSocket.send(message); //Sende neue Nachricht ueber Websocket Verbindung
            Log.i(this.getClass().getSimpleName(), "Send message " + message + ", status: " + status);
        }
    }

    private void login() {
        Log.d(this.getClass().getSimpleName(), "Login started with websocket.");
        GameMessage loginMessage = new GameMessage("LOGIN", auth, GameMessage.Status.OK, null, null);
        sendMessage(gson.toJson(loginMessage));
    }

    @Override
    public void onMessageReceived(String message) {
        try {
            GameMessage gameMessage = gson.fromJson(message, GameMessage.class);
            if ("LoginAnswer".equals(gameMessage.getType())) {
                if (gameMessage.getStatus() == GameMessage.Status.OK) {
                    //Login succeeded
                    Log.i(this.getClass().getSimpleName(), "Login succeeded");
                } else {
                    Log.w(this.getClass().getSimpleName(), "Login failed");
                }
            } else {
                Log.i(this.getClass().getSimpleName(), "Message was not of Type LoginAnswer - ignoring...");
            }
        } catch (JsonSyntaxException ex) {
            Log.w(this.getClass().getSimpleName(), "Couldn't cast message from backend, ignoring...\nMessage was: \"" + message + "\" printing stack trace...");
            ex.printStackTrace();
        }
    }

    public void close(Integer i, String r) {
        if (webSocket != null) {
            if (i == null) {
                i = 1000;
            }
            webSocket.close(i, r);
        }
    }

    private final class SocketListener extends WebSocketListener { //Listener der bei verschiedenen Websocket Ereignissen aufgerufen wird
        @Override
        public void onOpen(WebSocket socket, Response response) { //Aufgerufen, wenn neue Websocket Verbindung erzeugt wurde
            Log.d(WebSocketService.this.getClass().getSimpleName(), "Socket opened");
        }

        @Override
        public void onMessage(WebSocket socket, String text) {//Aufgerufen, wenn neue Textnachricht ueber Websocket Verbindung eintrifft
            Log.i(WebSocketService.this.getClass().getSimpleName(), "Received message: " + text);
            for (MessageListener listener : listeners) {
                listener.onMessageReceived(text);
            }
        }

        @Override
        public void onMessage(@NotNull WebSocket webSocket, ByteString bytes) {//Aufgerufen wenn neue Bytenachricht ueber Websocket Verbindung eintrifft
            Log.d(WebSocketService.this.getClass().getSimpleName(), "Receiving bytes : " + bytes.hex());
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, @NotNull String reason) {//Aufgerufen, wenn Websocket Verbindung geschlossen wird
            webSocket.close(1000, null);
            WebSocketService.this.webSocket = null;
            Log.d(WebSocketService.this.getClass().getSimpleName(), "closing: " + reason);
        }

        @Override
        public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {//Aufgerufen, nachdem Websocket Verbindung geschlossen wurde
            Log.d(WebSocketService.this.getClass().getSimpleName(), "closing: " + reason);
        }

        @Override
        public void onFailure(@NotNull WebSocket webSocket, Throwable t, Response response) {//Aufgerufen, wenn Fehler bei Websocket Verbindung auftritt
            Log.e(WebSocketService.this.getClass().getSimpleName(), "Error : " + t.getMessage());
            t.printStackTrace();
        }
    }

    /**
     * Register a {@link MessageListener} that will be called when a new message arrives
     * @param listener  the listener to be called
     */
    public void registerListener(MessageListener listener){
        if(listener!=null){
            listeners.add(listener);
        }
    }

    public void deregisterListener(MessageListener listener){
        listeners.remove(listener);
    }
}