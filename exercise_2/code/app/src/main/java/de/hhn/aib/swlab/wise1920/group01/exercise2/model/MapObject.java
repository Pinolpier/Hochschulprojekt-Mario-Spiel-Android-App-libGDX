package de.hhn.aib.swlab.wise1920.group01.exercise2.model;

import android.location.Location;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.simplefastpoint.LabelledGeoPoint;

public class MapObject extends LabelledGeoPoint {
    String description;

    /**
     * @param aLatitude   latitude of the object
     * @param aLongitude  longitude of the object
     * @param description description of the object, must not be {code null} if it is {code null} anyways it will be replaced by an empty string
     */
    public MapObject(double aLatitude, double aLongitude, String description) {
        super(aLatitude, aLongitude);
        setDescription(description);
    }

    /**
     * @param aLocation   the objects location as a Location Object
     * @param description description of the object, must not be {code null} if it is {code null} anyways it will be replaced by an empty string
     * @see Location
     */
    public MapObject(Location aLocation, String description) {
        super(aLocation);
        setDescription(description);
    }

    /**
     * @param aGeopoint   get a MapObject by adding information to an existing GeoPoint
     * @param description description of the object, must not be {code null} if it is {code null} anyways it will be replaced by an empty string
     * @see GeoPoint
     */
    public MapObject(GeoPoint aGeopoint, String description) {
        super(aGeopoint);
        setDescription(description);
    }

    /**
     * @param aLatitude   latitude of the object
     * @param aLongitude  longitude of the object
     * @param aLabel
     * @param description description of the object, must not be {code null} if it is {code null} anyways it will be replaced by an empty string
     */
    public MapObject(double aLatitude, double aLongitude, String aLabel, String description) {
        super(aLatitude, aLongitude, aLabel);
        setDescription(description);
    }

    /**
     * @return the objects description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description description of the object, must not be {code null} if it is {code null} anyways it will be replaced by an empty string
     */
    public void setDescription(String description) {
        if (description != null) {
            this.description = description;
        } else {
            this.description = "";
        }
    }
}
