package de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.extensions;

import java.util.ArrayList;

import de.hhn.aib.swlab.wise1920.group01.exercise2.model.MapObject;

public interface PoisReceivedInterface {
    void onSuccess(ArrayList<MapObject> poiElements);

    void onFailure();
}
