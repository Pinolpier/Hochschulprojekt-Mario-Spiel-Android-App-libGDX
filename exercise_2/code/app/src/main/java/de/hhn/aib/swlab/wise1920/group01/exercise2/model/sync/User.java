package de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("privacyRadius")
    @Expose
    private Integer privacyRadius;

    @SerializedName("username")
    @Expose
    private String username;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("password")
    @Expose
    private String password;

    private String jwtAuthorization;

    private Position position;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    //TODO Muss ID von außen gesetzt werden können?
    public void setId(String id) {
        this.id = id;
    }

    public Integer getPrivacyRadius() {
        return privacyRadius;
    }

    //TODO Bei Veränderung des PrivacyRadius muss zwingend der Server geupdatet werden! Mind: Methods are used in SyncService and no update shall be performed.
    public void setPrivacyRadius(Integer privacyRadius) {
        this.privacyRadius = privacyRadius;
    }

    public String getUsername() {
        return username;
    }

    //TODO Bei Veränderung des Username muss zwingend der Server geupdatet werden! Mind: Methods are used in SyncService and no update shall be performed.
    public void setUsername(String username) {
        this.username = username;
    }

    public String getDescription() {
        return description;
    }

    //TODO Bei Veränderung der Description muss zwingend der Server geupdatet werden! Mind: Methods are used in SyncService and no update shall be performed.
    public void setDescription(String description) {
        this.description = description;
    }

    public String getPassword() {
        return password;
    }

    //TODO Bei Veränderung des Passwort muss zwingend der Server geupdatet werden! Mind: Methods are used in SyncService and no update shall be performed.
    public void setPassword(String password) {
        this.password = password;
    }

    public String getJwtAuthorization() {
        return jwtAuthorization;
    }

    public void setJwtAuthorization(String jwtAuthorization) {
        this.jwtAuthorization = jwtAuthorization;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
}