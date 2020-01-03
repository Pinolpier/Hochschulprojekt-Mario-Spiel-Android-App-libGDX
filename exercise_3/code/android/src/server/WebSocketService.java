package server;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class WebSocketService extends Service {
    private static final String URL = "wss://g1-branch.stud.ex3.swlab-hhn.de/ws"; //URL vom Backend
    //URL vorher: "wss://tst-simple-example.stud.ex3.swlab-hhn.de/ws"
    private final IBinder binder = new WebSocketServiceBinder();
    private OkHttpClient client;
    private WebSocket webSocket;
    private List<MessageListener> listeners;

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
            Log.e("Service","onBind called");
        }
        return binder;
    }

    @Override
    public void onCreate(){
        listeners = new ArrayList<>();
        client = new OkHttpClient();
        Log.e("Service","OnCreate Executed");
    }

    private final class SocketListener extends WebSocketListener{ //Listener der bei verschiedenen Websocket Ereignissen aufgerufen wird
        @Override
        public void onOpen(WebSocket socket, Response response){ //Aufgerufen, wenn neue Websocket Verbindung erzeugt wurde
            Log.e(WebSocketService.this.getClass().getSimpleName(),"Socket opened");
        }

        @Override
        public void onMessage(WebSocket socket, String text){//Aufgerufen, wenn neue Textnachricht ueber Websocket Verbindung eintrifft
            Log.i(WebSocketService.this.getClass().getSimpleName(),"Received message: "+text);
            for(MessageListener listener : listeners){
                listener.onMessageReceived(text);
            }
        }

        @Override
        public void onMessage(@NotNull WebSocket webSocket, ByteString bytes) {//Aufgerufen wenn neue Bytenachricht ueber Websocket Verbindung eintrifft
            Log.e(WebSocketService.this.getClass().getSimpleName(),"Receiving bytes : " + bytes.hex());
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, @NotNull String reason) {//Aufgerufen, wenn Websocket Verbindung geschlossen wird
            webSocket.close(1000,null);
            WebSocketService.this.webSocket=null;
            Log.e(WebSocketService.this.getClass().getSimpleName(),"closing: "+ reason);
        }
        @Override
        public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason){//Aufgerufen, nachdem Websocket Verbindung geschlossen wurde
            Log.e(WebSocketService.this.getClass().getSimpleName(),"closing: "+ reason);
        }
        @Override
        public void onFailure(@NotNull WebSocket webSocket, Throwable t, Response response) {//Aufgerufen, wenn Fehler bei Websocket Verbindung auftritt
            Log.e(WebSocketService.this.getClass().getSimpleName(),"Error : " + t.getMessage());
            t.printStackTrace();
        }
    }

    /**
     * Sends a new message
     * @param message  the message to be send
     */
    public void sendMessage(String message){

        boolean status= webSocket.send(message); //Sende neue Nachricht ueber Websocket Verbindung
        Log.i(this.getClass().getSimpleName(),"Send message "+ message +", status: "+status);
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