package de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.extensions;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchResultNameDetailsDummy {
    @SerializedName("name")
    @Expose
    String name;

    @SerializedName("name:de")
    @Expose
    String nameDE;

    @SerializedName("name:en")
    @Expose
    String nameEN;

    public SearchResultNameDetailsDummy(String name, String nameDE, String nameEN) {
        this.name = name;
        this.nameDE = nameDE;
        this.nameEN = nameEN;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameDE() {
        return nameDE;
    }

    public void setNameDE(String nameDE) {
        this.nameDE = nameDE;
    }

    public String getNameEN() {
        return nameEN;
    }

    public void setNameEN(String nameEN) {
        this.nameEN = nameEN;
    }
}