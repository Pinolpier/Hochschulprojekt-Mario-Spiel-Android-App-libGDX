package de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.extensions;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class PoiElementsDummy {
    @SerializedName("elements")
    @Expose
    List<PoiDTO> poiDTOArray;

    public PoiElementsDummy(List<PoiDTO> poiDTOArray) {
        this.poiDTOArray = poiDTOArray;
    }

    public List<PoiDTO> getPoiDTOArray() {
        return poiDTOArray;
    }

    public void setPoiDTOArray(List<PoiDTO> poiDTOArray) {
        this.poiDTOArray = poiDTOArray;
    }
}
