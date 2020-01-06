package server.dtos;

import java.util.ArrayList;

public class GameMessage {
    private String type, authentication, gameId, payloadString;
    private Integer payloadInteger;
    private Status status;
    private ArrayList<String> stringList;

    public GameMessage(String type, String authentication, Status status, String gameId, ArrayList<String> stringList) {
        this.type = type;
        this.authentication = authentication;
        this.gameId = gameId;
        this.status = status;
        this.stringList = stringList;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAuthentication() {
        return authentication;
    }

    public void setAuthentication(String authentication) {
        this.authentication = authentication;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public ArrayList<String> getStringList() {
        return stringList;
    }

    public void setStringList(ArrayList<String> stringList) {
        this.stringList = stringList;
    }

    public String getPayloadString() {
        return payloadString;
    }

    public void setPayloadString(String payloadString) {
        this.payloadString = payloadString;
    }

    public Integer getPayloadInteger() {
        return payloadInteger;
    }

    public void setPayloadInteger(Integer payloadInteger) {
        this.payloadInteger = payloadInteger;
    }

    public enum Status {
        OK, FAILED
    }
}