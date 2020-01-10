package server;

/**
 * has to be implemented when a class wants to be informed about new messages from the websocket connection
 */
public interface MessageListener {
    void onMessageReceived(String message);
}