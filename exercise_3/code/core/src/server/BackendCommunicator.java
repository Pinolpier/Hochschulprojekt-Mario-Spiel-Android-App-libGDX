package server;

import server.dtos.GameMessage;

public interface BackendCommunicator {
    void sendMessage(GameMessage message);
}