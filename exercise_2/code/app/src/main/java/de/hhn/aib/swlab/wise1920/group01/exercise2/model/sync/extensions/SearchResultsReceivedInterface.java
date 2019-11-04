package de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.extensions;

import java.util.ArrayList;

import de.hhn.aib.swlab.wise1920.group01.exercise2.model.MapObject;

public interface SearchResultsReceivedInterface {
    void onSuccess(ArrayList<MapObject> searchResultsList);

    void onFailure();
}