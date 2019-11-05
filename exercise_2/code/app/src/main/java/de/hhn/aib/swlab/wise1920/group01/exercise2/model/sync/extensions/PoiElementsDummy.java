package de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.extensions;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PoiElementsDummy {
    @SerializedName("attraction")
    @Expose
    String attraction;

    @SerializedName("name")
    @Expose
    String name;
}
