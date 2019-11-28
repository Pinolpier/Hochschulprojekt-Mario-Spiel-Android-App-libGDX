package de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MapObjectDTO {
    @SerializedName("description")
    @Expose
    String description;
    @SerializedName("currentLocation")
    @Expose
    Position position;
    @SerializedName("name")
    @Expose
    private String name;

    public MapObjectDTO(String name, String description, Position position) {
        this.name = name;
        this.description = description;
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
}