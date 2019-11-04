package de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.extensions;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FuelDummy {
    @SerializedName("ok")
    @Expose
    String statusCode;

    @SerializedName("message")
    @Expose
    String errorMessage;

    @SerializedName("stations")
    @Expose
    List<GasStationDummy> stationsAround;

    public FuelDummy(String statusCode, String errorMessage, List<GasStationDummy> stationsAround) {
        this.statusCode = statusCode;
        this.errorMessage = errorMessage;
        this.stationsAround = stationsAround;
    }

    public FuelDummy(String statusCode, List<GasStationDummy> stationsAround) {
        this.statusCode = statusCode;
        this.stationsAround = stationsAround;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public List<GasStationDummy> getStationsAround() {
        return stationsAround;
    }

    public void setStationsAround(List<GasStationDummy> stationsAround) {
        this.stationsAround = stationsAround;
    }
}