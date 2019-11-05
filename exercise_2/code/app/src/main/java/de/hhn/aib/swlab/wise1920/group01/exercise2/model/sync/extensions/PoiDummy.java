package de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.extensions;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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

    @SerializedName("type")
    @Expose
    String type;

    public PoiDummy(Double lat, Double lon, PoiTagsDummy poiTagsList, String type) {
        this.lat = lat;
        this.lon = lon;
        this.poiTagsList = poiTagsList;
        this.type = type;
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

    public String getType() {
        return type;
    }
}
