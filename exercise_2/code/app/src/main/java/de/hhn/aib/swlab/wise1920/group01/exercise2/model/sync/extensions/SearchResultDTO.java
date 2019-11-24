package de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.extensions;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchResultDTO {
    @SerializedName("display_name")
    @Expose
    String description;

    @SerializedName("lat")
    @Expose
    Double latitude;

    @SerializedName("lon")
    @Expose
    Double longitude;

    public SearchResultDTO(String description, Double latitude, Double longitude) {
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}