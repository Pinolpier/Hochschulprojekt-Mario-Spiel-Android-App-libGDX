package de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync;

import de.hhn.aib.swlab.wise1920.group01.exercise2.model.MapObject;

public interface UsersAroundReceivedInterface {
    void onSuccess(MapObject[] usersAround);

    void onFailure();
}