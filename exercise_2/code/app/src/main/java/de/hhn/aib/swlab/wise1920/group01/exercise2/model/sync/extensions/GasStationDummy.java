package de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.extensions;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GasStationDummy {
    @SerializedName("name")
    @Expose
    String name;

    @SerializedName("brand")
    @Expose
    String brand;

    @SerializedName("lat")
    @Expose
    Double latitude;

    @SerializedName("lng")
    @Expose
    Double longitude;

    @SerializedName("diesel")
    @Expose
    Double diesel;

    @SerializedName("e5")
    @Expose
    Double e5;

    @SerializedName("e10")
    @Expose
    Double e10;

    @SerializedName("isOpen")
    @Expose
    Boolean isOpen;

    @SerializedName("street")
    @Expose
    String street;

    @SerializedName("place")
    @Expose
    String place;

    @SerializedName("houseNumber")
    @Expose
    String houseNumber;

    @SerializedName("postCode")
    @Expose
    String postCode;


    public GasStationDummy(String name, String brand, Double latitude, Double longitude, Double diesel, Double e5, Double e10, Boolean isOpen, String street, String place, String houseNumber, String postCode) {
        this.name = name;
        this.brand = brand;
        this.latitude = latitude;
        this.longitude = longitude;
        this.diesel = diesel;
        this.e5 = e5;
        this.e10 = e10;
        this.isOpen = isOpen;
        this.street = street;
        this.place = place;
        this.houseNumber = houseNumber;
        this.postCode = postCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
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

    public Double getDiesel() {
        return diesel;
    }

    public void setDiesel(Double diesel) {
        this.diesel = diesel;
    }

    public Double getE5() {
        return e5;
    }

    public void setE5(Double e5) {
        this.e5 = e5;
    }

    public Double getE10() {
        return e10;
    }

    public void setE10(Double e10) {
        this.e10 = e10;
    }

    public Boolean getOpen() {
        return isOpen;
    }

    public void setOpen(Boolean open) {
        isOpen = open;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }
}
