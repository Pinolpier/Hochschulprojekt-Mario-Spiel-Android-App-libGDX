package de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.extensions;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class PoiDummy {
    @SerializedName("lat")
    @Expose
    Double lat;

    @SerializedName("lon")
    @Expose
    Double lon;

    @SerializedName("tags")
    @Expose
    PoiTagsDummy poiTagsDummy;

    @SerializedName("type")
    @Expose
    String type;

    @SerializedName("id")
    @Expose
    Long id;

    @SerializedName("nodes")
    @Expose
    ArrayList<Long> nodesArrayList;

    @SerializedName("members")
    @Expose
    ArrayList<PoiDummy> poiMembers;

    @SerializedName("ref")
    @Expose
    Long ref;



    public PoiDummy(Double lat, Double lon, PoiTagsDummy poiTagsDummy, String type, long id, ArrayList<Long> nodesArrayList,ArrayList<PoiDummy> poiMembers,long ref) {
        this.lat = lat;
        this.lon = lon;
        this.poiTagsDummy = poiTagsDummy;
        this.type = type;
        this.id = id;
        this.nodesArrayList=nodesArrayList;
        this.poiMembers = poiMembers;
        this.ref = ref;
    }

    public ArrayList<PoiDummy> getPoiMembers() {
        return poiMembers;
    }

    public long getRef(){
        return ref;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ArrayList<Long> getNodesArrayList() {
        return nodesArrayList;
    }

    public void setNodesArrayList(ArrayList<Long> nodesArrayList) {
        this.nodesArrayList = nodesArrayList;
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

    public PoiTagsDummy getPoiTagsDummy() {
        return poiTagsDummy;
    }

    public void setPoiTagsDummy(PoiTagsDummy poiTagsDummy) {
        this.poiTagsDummy = poiTagsDummy;
    }

    public String getType() {
        return type;
    }
}
