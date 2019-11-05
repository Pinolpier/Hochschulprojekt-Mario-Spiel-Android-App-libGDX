package de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.extensions;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class PoiElementsDummy {
    @SerializedName("elements")
    @Expose
    List<PoiDummy> poiDummyArray;

    public PoiElementsDummy(List<PoiDummy> poiDummyArray) {
        this.poiDummyArray = poiDummyArray;
    }

    public List<PoiDummy> getPoiDummyArray() {
        return poiDummyArray;
    }

    public void setPoiDummyArray(List<PoiDummy> poiDummyArray) {
        this.poiDummyArray = poiDummyArray;
    }
}
