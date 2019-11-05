package de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.extensions;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PoiDummy {
    @SerializedName("lat")
    @Expose
    Double lat;

    @SerializedName("lon")
    @Expose
    Double lon;

    @SerializedName("tags")
    @Expose
    PoiTagsDummy poiTagsList;

    public PoiDummy(Double lat, Double lon, PoiTagsDummy poiTagsList) {
        this.lat = lat;
        this.lon = lon;
        this.poiTagsList = poiTagsList;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public PoiTagsDummy getPoiTagsList() {
        return poiTagsList;
    }

    public void setPoiTagsList(PoiTagsDummy poiTagsList) {
        this.poiTagsList = poiTagsList;
    }
}
