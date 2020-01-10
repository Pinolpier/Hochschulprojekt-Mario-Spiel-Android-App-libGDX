package server;

import server.dtos.GameMessage;

/**
 * used to access platform specific code within the core project that only runs java.
 * Needed for access to WebSocketService that extends an Android Service
 */
public interface BackendCommunicator {
    void sendMessage(GameMessage message);

    void stopGame(boolean sound);
}