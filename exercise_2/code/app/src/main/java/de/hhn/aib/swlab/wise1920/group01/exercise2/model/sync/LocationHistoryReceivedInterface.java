package de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync;

import java.util.ArrayList;

public interface LocationHistoryReceivedInterface {
    void onSuccess(ArrayList<TimestampedPosition> locationHistory);

    void onFailure();
}