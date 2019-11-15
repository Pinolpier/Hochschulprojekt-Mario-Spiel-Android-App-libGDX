package de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync;

import java.util.ArrayList;

import de.hhn.aib.swlab.wise1920.group01.exercise2.model.MapObject;

public interface UsersAroundReceivedInterface {
    void onSuccess(ArrayList<MapObject> usersAround);

    void onFailure();
}